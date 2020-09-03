/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2020, Vertigo.io, team@vertigo.io
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
package io.vertigo.struts2;

import io.vertigo.core.node.config.DefinitionProviderConfig;
import io.vertigo.core.node.config.Features;
import io.vertigo.datamodel.impl.smarttype.ModelDefinitionProvider;
import io.vertigo.struts2.boot.initializer.TestStruts2MasterDataDefinitionProvider;
import io.vertigo.struts2.data.Struts2TestSmartTypes;
import io.vertigo.struts2.data.dao.movies.MovieDAO;
import io.vertigo.struts2.services.movies.MovieServices;
import io.vertigo.struts2.services.movies.MovieServicesImpl;
import io.vertigo.struts2.services.users.UserServices;
import io.vertigo.struts2.services.users.UserServicesImpl;

public class TestStrus2Features extends Features<TestStrus2Features> {

	public TestStrus2Features() {
		super("test-vertigo-struts2");
	}

	@Override
	protected void buildFeatures() {
		getModuleConfigBuilder()
				.addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
						.addDefinitionResource("smarttypes", Struts2TestSmartTypes.class.getName())
						.addDefinitionResource("dtobjects", "io.vertigo.struts2.data.domain.DtDefinitions")
						.build())
				.addDefinitionProvider(TestStruts2MasterDataDefinitionProvider.class)
				.addComponent(MovieDAO.class)
				.addComponent(MovieServices.class, MovieServicesImpl.class)
				.addComponent(UserServices.class, UserServicesImpl.class)
				.build();
	}

}
