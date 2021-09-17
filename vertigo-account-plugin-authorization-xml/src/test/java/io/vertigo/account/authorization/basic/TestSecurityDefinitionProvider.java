/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2019, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.vertigo.account.authorization.basic;

import java.util.Collections;
import java.util.List;

import io.vertigo.account.authorization.basic.SecurityNames.Roles;
import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.util.ListBuilder;

public final class TestSecurityDefinitionProvider implements io.vertigo.core.node.definition.SimpleDefinitionProvider {

	@Override
	public List<Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
		return new ListBuilder<Definition>()
				.add(createRole(SecurityNames.Roles.RbAdmin))
				.add(createRole(SecurityNames.Roles.RbUser))
				.add(createRole(SecurityNames.Roles.RbManager))
				.add(createRole(SecurityNames.Roles.RbSecretary))
				.build();
	}

	private static RoleBasic createRole(final Roles name) {
		final String description = name.name();
		return new RoleBasic(name.name(), description, Collections.emptyList());
	}
}
