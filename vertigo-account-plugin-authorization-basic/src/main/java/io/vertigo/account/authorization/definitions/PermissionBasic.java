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

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;

/**
 * Une permission est l'association d'une opération et d'une ressource.
 *
 * @author prahmoune
 */
@DefinitionPrefix(PermissionBasic.PREFIX)
public final class PermissionBasic extends AbstractDefinition {
	public static final String PREFIX = "Prm";
	private final String operation;
	private final String filter;

	/**
	 * Constructor.
	 *
	 * @param name Permission name
	 * @param operation Operation
	 * @param filter Filter used to check permission
	 */
	public PermissionBasic(final String name, final String operation, final String filter) {
		super(name);
		//---
		Assertion.check().isNotNull(operation);
		Assertion.check().isNotBlank(filter);
		//-----
		this.operation = operation;
		this.filter = filter;
	}

	/**
	 * @return Filter used to check permission
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @return Operation
	 */
	public String getOperation() {
		return operation;
	}

}
