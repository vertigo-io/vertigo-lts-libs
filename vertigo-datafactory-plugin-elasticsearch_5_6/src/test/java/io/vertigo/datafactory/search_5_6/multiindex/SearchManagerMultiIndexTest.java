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
package io.vertigo.datafactory.search_5_6.multiindex;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.DefinitionProviderConfig;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.datafactory.DataFactoryFeatures;
import io.vertigo.datafactory.collections.ListFilter;
import io.vertigo.datafactory.collections.model.FacetedQueryResult;
import io.vertigo.datafactory.impl.search.dsl.DslListFilterBuilder;
import io.vertigo.datafactory.search.SearchManager;
import io.vertigo.datafactory.search.data.ItemSearchClient;
import io.vertigo.datafactory.search.data.TestSearchSmartTypes;
import io.vertigo.datafactory.search.data.domain.Item;
import io.vertigo.datafactory.search.data.domain.ItemDataBase;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datafactory.search.model.SearchIndex;
import io.vertigo.datafactory.search.model.SearchQuery;
import io.vertigo.datamodel.DataModelFeatures;
import io.vertigo.datamodel.impl.smarttype.ModelDefinitionProvider;
import io.vertigo.datamodel.data.model.DataObject;

/**
 * @author  npiedeloup
 */
public class SearchManagerMultiIndexTest {
	//Index
	private static final String IDX_ITEM = "IdxItem";

	/** Manager de recherche. */
	@Inject
	protected SearchManager searchManager;

	private ItemDataBase itemDataBase;

	private AutoCloseableNode node;

	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//---
		itemDataBase = new ItemDataBase();
	}

	@AfterEach
	public final void tearDown() {
		if (node != null) {
			node.close();
		}
	}

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.withLocales("fr_FR")
						.build())
				.addModule(new CommonsFeatures()
						.build())
				.addModule(new DataModelFeatures().build())
				.addModule(new DataFactoryFeatures()
						.withSearch()
						.addPlugin(io.vertigo.datafactory.plugins.search.elasticsearch_5_6.embedded.ESEmbeddedSearchServicesPlugin.class,
								Param.of("home", "io/vertigo/datafactory/search_5_6/indexconfig"),
								Param.of("config.file", "io/vertigo/datafactory/search_5_6/indexconfig/elasticsearch.yml"),
								Param.of("envIndex", "TuTest"),
								Param.of("rowsPerQuery", "50"))
						.build())
				.addModule(ModuleConfig.builder("myApp")
						.addComponent(ItemSearchClient.class)
						.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
								.addDefinitionResource("smarttypes", TestSearchSmartTypes.class.getName())
								.addDefinitionResource("dtobjects", "io.vertigo.datafactory.search.data.DtDefinitions")
								.build())
						.build())
				.build();
	}

	/**
	 * Test de création de n enregistrements dans l'index.
	 * La création s'effectue dans une seule transaction mais sur deux indexes.
	 * Vérifie la capacité du système à gérer plusieurs indexes.
	 */
	@Test
	public void testIndex() {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();
		final SearchIndexDefinition itemIndexDefinition = definitionSpace.resolve(IDX_ITEM, SearchIndexDefinition.class);

		for (final Item item : itemDataBase.getAllItems()) {
			final SearchIndex<Item, Item> index = SearchIndex.createIndex(itemIndexDefinition, item.getUID(), item);
			searchManager.put(itemIndexDefinition, index);
		}
		waitAndExpectIndexation(itemDataBase.size(), itemIndexDefinition);
	}

	/**
	 * Test de création nettoyage de l'index.
	 * La création s'effectue dans une seule transaction.
	 */
	@Test
	public void testClean() {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();
		final SearchIndexDefinition itemIndexDefinition = definitionSpace.resolve(IDX_ITEM, SearchIndexDefinition.class);
		final ListFilter removeQuery = ListFilter.of("*:*");
		searchManager.removeAll(itemIndexDefinition, removeQuery);

		waitAndExpectIndexation(0, itemIndexDefinition);
	}

	private long query(final String query, final SearchIndexDefinition indexDefinition) {
		//recherche
		final SearchQuery searchQuery = SearchQuery.builder(query, DslListFilterBuilder.class)
				.withCriteria("")
				.build();
		final FacetedQueryResult<DataObject, SearchQuery> result = searchManager.loadList(indexDefinition, searchQuery, null);
		return result.getCount();
	}

	private void waitAndExpectIndexation(final long expectedCount, final SearchIndexDefinition indexDefinition) {
		final long time = System.currentTimeMillis();
		long size = -1;
		try {
			do {
				Thread.sleep(100); //wait index was done

				size = query("*:*", indexDefinition);
				if (size == expectedCount) {
					break; //si le nombre est atteint on sort.
				}

			} while (System.currentTimeMillis() - time < 5000);//timeout 5s
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt(); //si interrupt on relance
		}
		Assertions.assertEquals(expectedCount, size);
	}

}
