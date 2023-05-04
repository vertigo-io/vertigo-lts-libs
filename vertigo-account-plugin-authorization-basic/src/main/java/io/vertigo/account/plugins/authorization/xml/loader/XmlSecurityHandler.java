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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import io.vertigo.account.authorization.definitions.PermissionBasic;
import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.util.StringUtil;

/**
 * @author npiedeloup
 */
final class XmlSecurityHandler extends DefaultHandler {
	private enum TagName {
		authorisationConfig, permission, role;
	}

	private enum AttrsName {
		id, operation, filter, description, permission, name, ref;
	}

	private final Map<String, PermissionBasic> permissions = new HashMap<>();
	private final List<RoleBasic> roles = new ArrayList<>();
	private final List<String> permissionsRef = new ArrayList<>();
	private final String[] currentRoleAttributes = new String[2];
	private boolean isInRole;

	List<PermissionBasic> getPermissions() {
		return new ArrayList<>(permissions.values());
	}

	List<RoleBasic> getRoles() {
		return roles;
	}

	@Override
	public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes attrs) {
		switch (TagName.valueOf(qName)) {
			case authorisationConfig:
				break;
			case permission:
				if (!isInRole) {
					final String prmId = convertDefinitionId(attrs.getValue(AttrsName.id.name()).trim());
					// it's a real permission so we handle it
					permissions.put(prmId, supplyPermissions(prmId,
							convertOperations(attrs.getValue(AttrsName.operation.name()).trim()),
							attrs.getValue(AttrsName.filter.name()).trim()));
				} else {
					// we are in a role so we append to the list of references
					permissionsRef.add(convertDefinitionId(attrs.getValue(AttrsName.ref.name())));
				}
				break;
			case role:
				isInRole = true;
				currentRoleAttributes[0] = convertDefinitionId(attrs.getValue(AttrsName.name.name()).trim(), "R_", RoleBasic.PREFIX.toUpperCase() + "_");
				currentRoleAttributes[1] = attrs.getValue(AttrsName.description.name()).trim();
				break;
			default:
		}
	}

	private String convertOperations(final String operations) {
		return Stream.of(operations.split("\\|")).map(StringUtil::constToUpperCamelCase).collect(Collectors.joining("|"));
	}

	private String convertDefinitionId(final String defId) {
		return StringUtil.constToUpperCamelCase(defId);
	}

	private String convertDefinitionId(final String defId, final String prefix, final String newPrefix) {
		return convertDefinitionId(newPrefix + defId.substring(prefix.length()));
	}

	@Override
	public void endElement(final String namespaceURI, final String localName, final String qName) {
		switch (TagName.valueOf(qName)) {
			case authorisationConfig:
			case permission:
				break;
			case role:
				Assertion.check().isNotNull(currentRoleAttributes);
				// ---
				roles.add(supplyRole(
						currentRoleAttributes[0],
						currentRoleAttributes[1],
						new ArrayList<>(permissionsRef)));
				permissionsRef.clear();
				isInRole = false;
				break;
			default:
		}
	}

	//case of <permission id="PRM_READ_ALL_PRODUCTSè" operation="READ" filter="/products/.*" description="Lire tous les produits"/>
	private static PermissionBasic supplyPermissions(final String id, final String operation, final String filter) {
		return new PermissionBasic(id, operation, filter);
	}

	private RoleBasic supplyRole(final String name, final String description, final List<String> myPermRefs) {
		final List<PermissionBasic> rolePermissions = myPermRefs.stream()
				.map(permissionName -> permissions.computeIfAbsent(permissionName, p -> {
					throw new VSystemException("Can't found {0} referenced by {1}", p, name);
				})).collect(Collectors.toList());
		return new RoleBasic(name, description, rolePermissions);
	}

}
