/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2022, Vertigo.io, team@vertigo.io
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
package io.vertigo.account.authorization.definitions;

import java.util.List;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;

/**
 * Un rôle est la réunion d'un ensemble de permissions.
 * Un utilisateur peut avoir  plusieurs rôles.
 *
 * @author prahmoune
 */
@DefinitionPrefix(RoleBasic.PREFIX)
public final class RoleBasic extends AbstractDefinition {
	public static final String PREFIX = "Rb";
	private final String description;
	private final List<PermissionBasic> permissions;

	/**
	 * Constructor.
	 *
	 * @param name Nom du rôle
	 * @param description Description du rôle
	 * @param permissions Liste des permissions associées au rôle
	 */
	public RoleBasic(final String name, final String description, final List<PermissionBasic> permissions) {
		super(name);
		//---
		Assertion.check().isNotBlank(description);
		Assertion.check().isNotNull(permissions);
		//-----
		this.description = description;
		this.permissions = permissions;
	}

	/**
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Liste des permissions
	 */
	public List<PermissionBasic> getPermissions() {
		return permissions;
	}
}
