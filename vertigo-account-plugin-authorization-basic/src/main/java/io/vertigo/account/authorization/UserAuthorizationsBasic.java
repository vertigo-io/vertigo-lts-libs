/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2021, Vertigo.io, team@vertigo.io
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
package io.vertigo.account.authorization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.vertigo.account.authorization.definitions.PermissionBasic;
import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.definition.DefinitionReference;

/**
 * This class list User's Authorizations.
 *
 * @author  pchretien, npiedeloup
 */
public final class UserAuthorizationsBasic implements Serializable {

	private static final long serialVersionUID = -7924146007592711123L;

	/**
	 * All authorizations list of this user (global and keyConcept)
	 */
	private final Map<String, DefinitionReference<PermissionBasic>> permissionRefs = new HashMap<>();

	/**
	 * Accepted roles for this user.
	 * Use for asc-compatibility.
	 */
	private final Set<DefinitionReference<RoleBasic>> roleRefs = new HashSet<>();

	private final Map<String, List<Serializable>> mySecurityKeys = new HashMap<>();

	//===========================================================================
	//=======================GESTION DES ROLES===================================
	//===========================================================================
	/**
	 * Add a role to this User.
	 * Role must be previously declared.
	 *
	 * @param role Role to add
	 * @return this UserAuthorizations
	 */
	public UserAuthorizationsBasic addRole(final RoleBasic role) {
		Assertion.check().isNotNull(role);
		//-----
		roleRefs.add(new DefinitionReference<>(role));
		role.getPermissions()
				.forEach(this::addPermission);
		return this;
	}

	/**
	 * Return roles set of this user.
	 * @return roles set
	 */
	public Set<RoleBasic> getRoles() {
		return roleRefs.stream()
				.map(DefinitionReference::get)
				.collect(Collectors.toSet());
	}

	/**
	 * @param role Role
	 * @return if user has this role
	 */
	public boolean hasRole(final RoleBasic role) {
		Assertion.check().isNotNull(role);
		//-----
		return roleRefs.contains(new DefinitionReference<>(role));
	}

	/**
	 * Clear all roles on this user. (authorizations are cleared too)
	 * Warning : no more rights after that.
	 * @return this UserAuthorizations
	 */
	public UserAuthorizationsBasic clearRoles() {
		roleRefs.clear();
		clearPermissions();
		return this;
	}

	/**
	 * Add a authorization to this User.
	 * Authorization must be previously declared.
	 *
	 * @param permission Authorization to add
	 * @return this UserPermissionV1s
	 */
	public UserAuthorizationsBasic addPermission(final PermissionBasic permission) {
		Assertion.check().isNotNull(permission);
		//-----
		final DefinitionReference<PermissionBasic> definitionReference = new DefinitionReference<>(permission);
		permissionRefs.put(permission.getName(), definitionReference);
		return this;
	}

	/**
	 * Return permissions set of this user.
	 * @return permissions set
	 */
	public Set<PermissionBasic> getPermissions() {
		return permissionRefs.values().stream()
				.map(DefinitionReference::get)
				.collect(Collectors.toSet());
	}

	/**
	 * Clear all authorization on this user. (but only authorization : roles aren't cleared)
	 * Warning : no more rights after that.
	 * @return this UserAuthorizations
	 */
	public UserAuthorizationsBasic clearPermissions() {
		permissionRefs.clear();
		return this;
	}

	/**
	 * Return the security keys of this user.
	 * Used for data dependent security rules.
	 * @return User's security keys.
	 */
	public Map<String, List<Serializable>> getSecurityKeys() {
		return mySecurityKeys;
	}

	/**
	 * Add a security key part of his security perimeter.
	 * A security key can be multi-valued (then withSecurityKeys is call multiple times).
	 * Value should be an array if this securityKey is a tree (hierarchical) key.
	 *
	 * @param securityKey Name
	 * @param value Value
	 * @return this UserAuthorizations
	 */
	public UserAuthorizationsBasic withSecurityKeys(final String securityKey, final Serializable value) {
		mySecurityKeys.computeIfAbsent(securityKey, v -> new ArrayList<>()).add(value);
		Assertion.check().isTrue(mySecurityKeys.get(securityKey).size() == 1, "SecurityKey ({0}) dont support multivalued values yet", securityKey);
		return this;
	}

	/**
	 * Clear Security Keys.
	 * Use when user change it security perimeter.
	 * @return this UserAuthorizations
	 */
	public UserAuthorizationsBasic clearSecurityKeys() {
		mySecurityKeys.clear();
		return this;
	}
}
