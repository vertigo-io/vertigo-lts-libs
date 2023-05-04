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
package io.vertigo.account.plugins.authorization.xml.loader;

import java.util.List;

import io.vertigo.account.authorization.definitions.PermissionBasic;
import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.core.lang.Assertion;

public class XmlSecurityDefinition {

	private final List<PermissionBasic> permissions;
	private final List<RoleBasic> roles;

	public XmlSecurityDefinition(final List<PermissionBasic> permissions, final List<RoleBasic> roles) {
		Assertion.check().isNotNull(permissions);
		Assertion.check().isNotNull(roles);
		//----
		this.permissions = permissions;
		this.roles = roles;
	}

	public List<PermissionBasic> getPermissions() {
		return permissions;
	}

	public List<RoleBasic> getRoles() {
		return roles;
	}

}
