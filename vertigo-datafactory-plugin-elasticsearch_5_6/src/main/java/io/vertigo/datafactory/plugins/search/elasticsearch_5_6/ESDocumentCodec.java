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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;

import io.vertigo.commons.codec.CodecManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.BasicTypeAdapter;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datafactory.search.model.SearchIndex;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.smarttype.definitions.SmartTypeDefinition;
import io.vertigo.datamodel.data.definitions.DataAccessor;
import io.vertigo.datamodel.data.definitions.DataDefinition;
import io.vertigo.datamodel.data.definitions.DataField;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.model.KeyConcept;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * Traduction bi directionnelle des objets SOLR en objets logique de recherche.
 * Pseudo Codec : asymétrique par le fait que ElasticSearch gère un objet différent en écriture et lecture.
 * L'objet lu ne contient pas les données indexées non stockées !
 * @author pchretien, npiedeloup
 */
final class ESDocumentCodec {
	/** FieldName containing Full result object. */
	protected static final String FULL_RESULT = "fullResult";
	protected static final String DOC_ID = "docId";

	//-----
	private final CodecManager codecManager;
	private final SmartTypeManager smartTypeManager;

	/**
	 * Constructor.
	 * @param codecManager Manager des codecs
	 * @param codecManager Manager de la modelisation (SmartTypes)
	 */
	ESDocumentCodec(final CodecManager codecManager, final SmartTypeManager smartTypeManager) {
		Assertion.check()
				.isNotNull(codecManager)
				.isNotNull(smartTypeManager);
		//-----
		this.codecManager = codecManager;
		this.smartTypeManager = smartTypeManager;
	}

	private <I extends DataObject> String encode(final I dto) {
		Assertion.check().isNotNull(dto);
		//-----
		final byte[] data = codecManager.getCompressedSerializationCodec().encode(dto);
		return codecManager.getBase64Codec().encode(data);
	}

	private <R extends DataObject> R decode(final String base64Data) {
		Assertion.check().isNotNull(base64Data);
		//-----
		final byte[] data = codecManager.getBase64Codec().decode(base64Data);
		return (R) codecManager.getCompressedSerializationCodec().decode(data);
	}

	/**
	 * Transformation d'un resultat ElasticSearch en un index.
	 * Les highlights sont ajoutés avant ou après (non determinable).
	 * @param <S> Type du sujet représenté par ce document
	 * @param <I> Type d'object indexé
	 * @param indexDefinition Definition de l'index
	 * @param searchHit Resultat ElasticSearch
	 * @return Objet logique de recherche
	 */
	<S extends KeyConcept, I extends DataObject> SearchIndex<S, I> searchHit2Index(final SearchIndexDefinition indexDefinition, final SearchHit searchHit) {
		/* On lit du document les données persistantes. */
		/* 1. UID */
		final String urn = searchHit.getId();
		final UID uid = io.vertigo.datamodel.data.model.UID.of(urn);

		/* 2 : Result stocké */
		final I resultDtObjectdtObject;
		if (searchHit.field(FULL_RESULT) == null) {
			resultDtObjectdtObject = decode((String) searchHit.getSource().get(FULL_RESULT));
		} else {
			resultDtObjectdtObject = decode(searchHit.field(FULL_RESULT).getValue());
		}
		//-----
		return SearchIndex.createIndex(indexDefinition, uid, resultDtObjectdtObject);
	}

	/**
	 * Transformation d'un index en un document ElasticSearch.
	 * @param <S> Type du sujet représenté par ce document
	 * @param <I> Type d'object indexé
	 * @param index Objet logique de recherche
	 * @return Document SOLR
	 * @throws IOException Json exception
	 */
	<S extends KeyConcept, I extends DataObject> XContentBuilder index2XContentBuilder(final SearchIndex<S, I> index) throws IOException {
		Assertion.check().isNotNull(index);
		//-----

		final DataDefinition dtDefinition = index.getDefinition().getIndexDtDefinition();
		final List<DataField> notStoredFields = getNotStoredFields(dtDefinition); //on ne copie pas les champs not stored dans le smartType
		notStoredFields.addAll(index.getDefinition().getIndexCopyToFields()); //on ne copie pas les champs (copyTo)
		final I dtResult;
		if (notStoredFields.isEmpty()) {
			dtResult = index.getIndexDtObject();
		} else {
			dtResult = cloneDto(dtDefinition, index.getIndexDtObject(), notStoredFields);
		}

		/* 2: Result stocké */
		final String result = encode(dtResult);

		/* 1 : UID */
		try (final XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()) {
			xContentBuilder.startObject()
					.field(FULL_RESULT, result)
					.field(DOC_ID, Serializable.class.cast(index.getUID().getId()));

			/* 3 : Les champs du dto index */
			final DataObject dtIndex = index.getIndexDtObject();
			final DataDefinition indexDtDefinition = DataModelUtil.findDataDefinition(dtIndex);
			final Set<DataField> copyToFields = index.getDefinition().getIndexCopyToFields();
			final Map<Class, BasicTypeAdapter> typeAdapters = smartTypeManager.getTypeAdapters("search");

			for (final DataField dtField : indexDtDefinition.getFields()) {
				if (!copyToFields.contains(dtField)) {//On index pas les copyFields
					final Object value = dtField.getDataAccessor().getValue(dtIndex);
					if (value != null) { //les valeurs null ne sont pas indexées => conséquence : on ne peut pas les rechercher
						final String indexFieldName = dtField.name();
						switch (dtField.smartTypeDefinition().getScope()) {
							case BASIC_TYPE:
								if (value instanceof String) {
									final String encodedValue = escapeInvalidUTF8Char((String) value);
									xContentBuilder.field(indexFieldName, encodedValue);
								} else {
									xContentBuilder.field(indexFieldName, value);
								}
								break;
							case VALUE_TYPE:
								final BasicTypeAdapter basicTypeAdapter = typeAdapters.get(dtField.smartTypeDefinition().getJavaClass());
								xContentBuilder.field(indexFieldName, basicTypeAdapter.toBasic(value));
								break;
							default:
								throw new IllegalArgumentException("Type de donnée non pris en charge pour l'indexation [" + dtField.smartTypeDefinition() + "].");
						}
					}
				}
			}
			return xContentBuilder.endObject();
		}
	}

	private static List<DataField> getNotStoredFields(final DataDefinition dtDefinition) {
		return dtDefinition.getFields().stream()
				.filter(dtField -> !isIndexStoredDomain(dtField.smartTypeDefinition()))
				.collect(Collectors.toList());
	}

	private static <I extends DataObject> I cloneDto(final DataDefinition dtDefinition, final I dto, final List<DataField> excludedFields) {
		final I clonedDto = (I) DataModelUtil.createDataObject(dtDefinition);
		for (final DataField dtField : dtDefinition.getFields()) {
			if (!excludedFields.contains(dtField)) {
				final DataAccessor dataAccessor = dtField.getDataAccessor();
				dataAccessor.setValue(clonedDto, dataAccessor.getValue(dto));
			}
		}
		return clonedDto;
	}

	private static boolean isIndexStoredDomain(final SmartTypeDefinition smartTypeDefinition) {
		final IndexType indexType = IndexType.readIndexType(smartTypeDefinition);
		return indexType.isIndexStored(); //is no specific indexType, the field should be stored
	}

	private static String escapeInvalidUTF8Char(final String value) {
		return value.replace('\uFFFF', ' ').replace('\uFFFE', ' '); //testé comme le plus rapide pour deux cas
	}
}
