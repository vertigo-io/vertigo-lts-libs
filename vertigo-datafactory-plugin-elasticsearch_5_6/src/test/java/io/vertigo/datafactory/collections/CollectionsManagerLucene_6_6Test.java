/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2023, Vertigo.io, team@vertigo.io
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
package io.vertigo.datafactory.collections;

import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.DefinitionProviderConfig;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.datafactory.DataFactoryFeatures;
import io.vertigo.datafactory.collections.data.TestCollectionsSmartTypes;
import io.vertigo.datamodel.DataModelFeatures;
import io.vertigo.datamodel.impl.smarttype.ModelDefinitionProvider;
import io.vertigo.datastore.DataStoreFeatures;

/**
 * @author npiedeloup
 */
public class CollectionsManagerLucene_6_6Test extends CollectionsManagerTest {

	@Override
	protected NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.build())
				.addModule(new CommonsFeatures().build())
				.addModule(new DataModelFeatures().build())
				.addModule(new DataStoreFeatures()
						.withCache()
						.withMemoryCache()
						.build())
				.addModule(new DataFactoryFeatures()
						.addPlugin(io.vertigo.datafactory.plugins.collections.lucene_6_6.LuceneIndexPlugin.class)
						.build())
				.addModule(ModuleConfig.builder("myApp")
						.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
								.addDefinitionResource("smarttypes", TestCollectionsSmartTypes.class.getName())
								.addDefinitionResource("dtobjects", "io.vertigo.datafactory.collections.data.DtDefinitions")
								.build())
						.build())
				.build();
	}
}
