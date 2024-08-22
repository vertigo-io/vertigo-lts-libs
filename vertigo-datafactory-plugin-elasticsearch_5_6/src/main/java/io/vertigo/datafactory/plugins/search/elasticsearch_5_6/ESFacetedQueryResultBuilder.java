/*
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2024, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.datafactory.plugins.search.elasticsearch_5_6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Builder;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datafactory.collections.ListFilter;
import io.vertigo.datafactory.collections.definitions.FacetDefinition;
import io.vertigo.datafactory.collections.definitions.FacetedQueryDefinition;
import io.vertigo.datafactory.collections.model.Facet;
import io.vertigo.datafactory.collections.model.FacetValue;
import io.vertigo.datafactory.collections.model.FacetedQueryResult;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datafactory.search.model.SearchIndex;
import io.vertigo.datafactory.search.model.SearchQuery;
import io.vertigo.datamodel.data.definitions.DataDefinition;
import io.vertigo.datamodel.data.definitions.DataField;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DataObject;

//vérifier
/**
 * Requête physique d'accès à ElasticSearch.
 * Le driver exécute les requêtes de façon synchrone dans le contexte transactionnelle de la ressource.
 * @author pchretien, npiedeloup
 * @param <I> Type de l'objet représentant l'index
 */
final class ESFacetedQueryResultBuilder<I extends DataObject> implements Builder<FacetedQueryResult<I, SearchQuery>> {

	private static final String TOPHITS_SUBAGGREAGTION_NAME = "top";
	private static final String EMPTY_TERM = "_empty_";

	private final ESDocumentCodec esDocumentCodec;
	private final SearchIndexDefinition indexDefinition;
	private final SearchResponse queryResponse;
	private final SearchQuery searchQuery;

	/**
	 * Constructor.
	 * @param esDocumentCodec Translation codec from Index Dto to document
	 * @param indexDefinition Index definition
	 * @param queryResponse ES Query response
	 * @param searchQuery Search query
	 */
	ESFacetedQueryResultBuilder(
			final ESDocumentCodec esDocumentCodec,
			final SearchIndexDefinition indexDefinition,
			final SearchResponse queryResponse,
			final SearchQuery searchQuery) {
		Assertion.check()
				.isNotNull(esDocumentCodec)
				.isNotNull(indexDefinition)
				.isNotNull(queryResponse)
				.isNotNull(searchQuery);
		//-----
		this.esDocumentCodec = esDocumentCodec;
		this.indexDefinition = indexDefinition;
		this.queryResponse = queryResponse;
		this.searchQuery = searchQuery;
	}

	/** {@inheritDoc} */
	@Override
	public FacetedQueryResult<I, SearchQuery> build() {
		final Map<I, Map<DataField, String>> resultHighlights = new HashMap<>();
		final Map<FacetValue, DtList<I>> resultCluster;
		final DtList<I> dtc = new DtList<>(indexDefinition.getIndexDtDefinition());
		if (searchQuery.isClusteringFacet()) {
			final Map<String, I> dtcIndex = new LinkedHashMap<>();
			resultCluster = createCluster(dtcIndex, resultHighlights);
			dtc.addAll(dtcIndex.values());
		} else {
			for (final SearchHit searchHit : queryResponse.getHits()) {
				final SearchIndex<?, I> index = esDocumentCodec.searchHit2Index(indexDefinition, searchHit);
				final I result = index.getIndexDtObject();
				dtc.add(result);
				final Map<DataField, String> highlights = createHighlight(searchHit, indexDefinition.getIndexDtDefinition());
				resultHighlights.put(result, highlights);
			}
			resultCluster = Collections.emptyMap();
		}
		//On fabrique à la volée le résultat.
		final List<Facet> facets = createFacetList(searchQuery, queryResponse);
		final long count = queryResponse.getHits().getTotalHits();
		return new FacetedQueryResult<>(
				searchQuery.getFacetedQuery(),
				count,
				dtc,
				facets,
				searchQuery.isClusteringFacet() ? Optional.of(searchQuery.getClusteringFacetDefinition()) : Optional.empty(),
				resultCluster,
				resultHighlights,
				searchQuery);

	}

	private Map<FacetValue, DtList<I>> createCluster(
			final Map<String, I> dtcIndex,
			final Map<I, Map<DataField, String>> resultHighlights) {
		final Map<FacetValue, DtList<I>> resultCluster = new LinkedHashMap<>();
		final FacetDefinition facetDefinition = searchQuery.getClusteringFacetDefinition();
		final Aggregation facetAggregation = queryResponse.getAggregations().get(facetDefinition.getName());
		if (facetDefinition.isRangeFacet()) {
			//Cas des facettes par 'range'
			final MultiBucketsAggregation multiBuckets = (MultiBucketsAggregation) facetAggregation;
			for (final FacetValue facetRange : facetDefinition.getFacetRanges()) {
				final Bucket value = getBucketByKey(multiBuckets, facetRange.code());
				populateCluster(value, facetRange, resultCluster, dtcIndex, resultHighlights);
			}
		} else {
			//Cas des facettes par 'term'
			final MultiBucketsAggregation multiBuckets = (MultiBucketsAggregation) facetAggregation;
			FacetValue facetValue;
			for (final Bucket bucket : multiBuckets.getBuckets()) {
				facetValue = createFacetTermValue(bucket, facetDefinition);
				populateCluster(bucket, facetValue, resultCluster, dtcIndex, resultHighlights);
			}
		}
		return resultCluster;
	}

	private static Bucket getBucketByKey(final MultiBucketsAggregation multiBuckets, final String facetName) {
		return multiBuckets.getBuckets()
				.stream()
				.filter(bucket -> bucket.getKeyAsString().equals(facetName))
				.findFirst()
				.orElseThrow(() -> new VSystemException("No facet {0} found in result", facetName));
	}

	private void populateCluster(
			final Bucket bucket,
			final FacetValue facetValue,
			final Map<FacetValue, DtList<I>> resultCluster,
			final Map<String, I> dtcIndex,
			final Map<I, Map<DataField, String>> resultHighlights) {
		final SearchHits facetSearchHits = ((TopHits) bucket.getAggregations().get(TOPHITS_SUBAGGREAGTION_NAME)).getHits();
		final DtList<I> facetDtc = new DtList<>(indexDefinition.getIndexDtDefinition());
		for (final SearchHit searchHit : facetSearchHits) {
			I result = dtcIndex.get(searchHit.getId());
			if (result == null) {
				final SearchIndex<?, I> index = esDocumentCodec.searchHit2Index(indexDefinition, searchHit);
				result = index.getIndexDtObject();
				dtcIndex.put(searchHit.getId(), result);
				final Map<DataField, String> highlights = createHighlight(searchHit, indexDefinition.getIndexDtDefinition());
				resultHighlights.put(result, highlights);
			}
			facetDtc.add(result);
		}
		resultCluster.put(facetValue, facetDtc);
	}

	private static Map<DataField, String> createHighlight(final SearchHit searchHit, final DataDefinition resultDtDefinition) {
		final Map<DataField, String> highlights = new HashMap<>();
		final Map<String, HighlightField> highlightsMap = searchHit.getHighlightFields();

		for (final Map.Entry<String, HighlightField> entry : highlightsMap.entrySet()) {
			final String fieldName = entry.getKey();
			if (resultDtDefinition.contains(fieldName)) { //We only keep highlighs match on result's fields
				final DataField dtField = resultDtDefinition.getField(fieldName);
				final StringBuilder sb = new StringBuilder();
				for (final Text fragment : entry.getValue().getFragments()) {
					sb.append("<hlfrag>").append(fragment).append("</hlfrag>");
				}
				highlights.put(dtField, sb.toString());
			}
		}
		return highlights;
	}

	private static List<Facet> createFacetList(final SearchQuery searchQuery, final SearchResponse queryResponse) {
		final List<Facet> facets = new ArrayList<>();
		if (searchQuery.getFacetedQuery().isPresent() && queryResponse.getAggregations() != null) {
			final FacetedQueryDefinition queryDefinition = searchQuery.getFacetedQuery().get().getDefinition();
			for (final FacetDefinition facetDefinition : queryDefinition.getFacetDefinitions()) {
				final Aggregation aggregation = obtainAggregation(queryResponse, facetDefinition.getName());
				if (aggregation != null) {
					final Facet facet = createFacet(facetDefinition, (MultiBucketsAggregation) aggregation);
					facets.add(facet);
				}
			}
		}
		return facets;
	}

	private static Aggregation obtainAggregation(final SearchResponse queryResponse, final String name) {
		final Filter filterAggregation = queryResponse.getAggregations().get(name + "Filter");
		if (filterAggregation != null) {
			return filterAggregation.getAggregations().get(name);
		}
		return queryResponse.getAggregations().get(name);
	}

	private static Facet createFacet(final FacetDefinition facetDefinition, final MultiBucketsAggregation aggregation) {
		if (facetDefinition.isRangeFacet()) {
			//Cas des facettes par 'range'
			return createFacetRange(facetDefinition, aggregation);
		}
		//Cas des facettes par 'term'
		return createTermFacet(facetDefinition, aggregation);
	}

	private static Facet createTermFacet(final FacetDefinition facetDefinition, final MultiBucketsAggregation multiBuckets) {
		final Map<FacetValue, Long> facetValues = new LinkedHashMap<>();
		FacetValue facetValue;
		for (final Bucket bucket : multiBuckets.getBuckets()) {
			facetValue = createFacetTermValue(bucket, facetDefinition);
			facetValues.put(facetValue, bucket.getDocCount());
		}

		return new Facet(facetDefinition, facetValues);
	}

	private static FacetValue createFacetTermValue(final Bucket value, final FacetDefinition facetDefinition) {
		final String valueAsString = value.getKeyAsString();
		final String term;
		final String query;
		if (!StringUtil.isBlank(valueAsString)) {
			term = valueAsString;
		} else {
			term = EMPTY_TERM;
		}
		if (valueAsString != null) {
			query = facetDefinition.getDataField().name() + ":\"" + valueAsString + "\"";
		} else {
			query = "!_exists_:" + facetDefinition.getDataField().name(); //only for null value, empty ones use FIELD:""
		}

		return new FacetValue(term, ListFilter.of(query), LocaleMessageText.of(term));

	}

	private static Facet createFacetRange(final FacetDefinition facetDefinition, final MultiBucketsAggregation rangeBuckets) {
		//Cas des facettes par range
		final Map<FacetValue, Long> rangeValues = new LinkedHashMap<>();
		for (final FacetValue facetRange : facetDefinition.getFacetRanges()) {
			final Bucket value = getBucketByKey(rangeBuckets, facetRange.code());
			rangeValues.put(facetRange, value.getDocCount());
		}
		return new Facet(facetDefinition, rangeValues);
	}

}
