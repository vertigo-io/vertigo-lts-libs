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
package io.vertigo.account.plugins.authorization.xml.loader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.account.authorization.definitions.PermissionBasic;
import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.config.DefinitionResourceConfig;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;
import io.vertigo.core.resource.ResourceManager;

/**
 * @author npiedeloup
 */
public final class XmlSecurityDefinitionProvider implements DefinitionProvider {
	private final ResourceManager resourceManager;
	private final List<DefinitionSupplier> definitionSuppliers;

	/**
	 * Constructor.
	 * @param resourceManager the resourceManager
	 */
	@Inject
	public XmlSecurityDefinitionProvider(final ResourceManager resourceManager) {
		Assertion.check().isNotNull(resourceManager);
		// -----
		this.resourceManager = resourceManager;
		definitionSuppliers = new ArrayList<>();
	}

	/** {@inheritDoc} */
	@Override
	public List<DefinitionSupplier> get(final DefinitionSpace definitionSpace) {
		return definitionSuppliers;
	}

	/** {@inheritDoc} */
	@Override
	public void addDefinitionResourceConfig(final DefinitionResourceConfig definitionResourceConfig) {
		Assertion.check().isTrue("securityXml".equals(definitionResourceConfig.getType()), "Type {0} not supported",
				definitionResourceConfig.getType());
		// -----
		registerDefinitions(new XmlSecurityLoader(resourceManager, definitionResourceConfig.getPath()).load());
	}

	private void registerDefinitions(final XmlSecurityDefinition xmlSecurityDefinition) {
		registerRoles(xmlSecurityDefinition.getRoles());
		registerPermissions(xmlSecurityDefinition.getPermissions());
	}

	private void registerRoles(final List<RoleBasic> roles) {
		//on register les authorizations globales
		roles
				.forEach(role -> definitionSuppliers.add(ds -> role));
	}

	private void registerPermissions(final List<PermissionBasic> permissions) {
		//on register les permissions
		permissions
				.forEach(perm -> definitionSuppliers.add(ds -> perm));
	}

}
