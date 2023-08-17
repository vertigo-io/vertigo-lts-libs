/*
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
package io.vertigo.account.impl.authorization.xml;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationBasicManager;
import io.vertigo.account.authorization.ResourceNameFactory;
import io.vertigo.account.authorization.UserAuthorizationsBasic;
import io.vertigo.account.authorization.definitions.PermissionBasic;
import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.account.security.UserSession;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.core.lang.Assertion;

/**
 * Implementation standard de la gestion centralisee des droits d'acces.
 *
 * @author npiedeloup
 */
public final class AuthorizationBasicManagerImpl implements AuthorizationBasicManager {
	private final Map<String, ResourceNameFactory> resourceNameFactories = new HashMap<>();

	private static final String USER_SESSION_ACL_KEY = "vertigo.account.authorizationsBasic";

	private final VSecurityManager securityManager;

	/**
	 * Constructor.
	 * @param securityManager Security manager
	 */
	@Inject
	public AuthorizationBasicManagerImpl(final VSecurityManager securityManager) {
		Assertion.check().isNotNull(securityManager);
		//-----
		this.securityManager = securityManager;
	}

	/** {@inheritDoc} */
	@Override
	public UserAuthorizationsBasic obtainUserAuthorizations() {
		return getUserAuthorizationsOpt().orElseThrow(() -> new IllegalArgumentException("Can't getUserAuthorizations, check your have create an UserSession before."));

	}

	private Optional<UserAuthorizationsBasic> getUserAuthorizationsOpt() {
		final Optional<UserSession> userSessionOpt = securityManager.getCurrentUserSession();
		if (userSessionOpt.isEmpty()) {
			// Si il n'y a pas de session alors pas d'autorisation.
			return Optional.empty();
		}
		UserAuthorizationsBasic userAuthorizations = userSessionOpt.get().getAttribute(USER_SESSION_ACL_KEY);
		if (userAuthorizations == null) {
			userAuthorizations = new UserAuthorizationsBasic();
			userSessionOpt.get().putAttribute(USER_SESSION_ACL_KEY, userAuthorizations);
		}
		return Optional.of(userAuthorizations);

	}

	/** {@inheritDoc} */
	@Override
	public boolean hasRole(final RoleBasic... authorizedRole) {
		Assertion.check().isNotNull(authorizedRole);
		//-----
		final Optional<UserAuthorizationsBasic> userPermissionsOpt = getUserAuthorizationsOpt();
		if (userPermissionsOpt.isEmpty()) {
			// Si il n'y a pas de session alors pas d'autorisation.
			return false;
		}
		if (authorizedRole.length == 0) {
			// Si il n'y a aucun role nécéssaire alors c'est bon.
			return true;
		}
		final UserAuthorizationsBasic userPermissions = userPermissionsOpt.get();
		for (final RoleBasic role : authorizedRole) {
			if (userPermissions.hasRole(role)) {
				return true;
			}
		}
		return false;

	}

	/** {@inheritDoc} */
	@Override
	public boolean isAuthorized(final String resource, final String operation) {
		// Note: il s'agit d'une implementation naïve non optimisee,
		// réalisée pour valider le modèle
		final Optional<UserAuthorizationsBasic> userPermissionsOpt = getUserAuthorizationsOpt();
		if (userPermissionsOpt.isEmpty()) {
			// Si il n'y a pas de session alors pas d'autorisation.
			return false;
		}
		final UserAuthorizationsBasic userPermissions = userPermissionsOpt.get();
		final Map<String, List<Serializable>> securityKeys = userPermissions.getSecurityKeys();

		return userPermissions.getPermissions().stream()
				.anyMatch(permission -> isAuthorized(permission, resource, operation, securityKeys));
	}

	private static boolean isAuthorized(final PermissionBasic permission, final String resource, final String operation, final Map<String, List<Serializable>> securityKeys) {
		final String filter = permission.getFilter();
		final String personalFilter = applySecurityKeys(filter, securityKeys);
		final Pattern pFilter = Pattern.compile(personalFilter);
		final Pattern pOperation = Pattern.compile(permission.getOperation());
		return pFilter.matcher(resource).matches() && pOperation.matcher(operation).matches();
	}

	private static String applySecurityKeys(final String filter, final Map<String, List<Serializable>> securityKeys) {
		final StringBuilder personalFilter = new StringBuilder();
		int previousIndex = 0;
		int nextIndex = filter.indexOf("${", previousIndex);
		while (nextIndex >= 0) {
			personalFilter.append(filter.substring(previousIndex, nextIndex));
			final int endIndex = filter.indexOf('}', nextIndex + "${".length());
			Assertion.check().isTrue(endIndex >= nextIndex, "missing \\} : {0} à {1}", filter, nextIndex);
			final String key = filter.substring(nextIndex + "${".length(), endIndex);
			appendSecurityKeyValue(personalFilter, key, securityKeys);
			previousIndex = endIndex + "}".length();
			nextIndex = filter.indexOf("${", previousIndex);
		}
		if (previousIndex < filter.length()) {
			personalFilter.append(filter.substring(previousIndex, filter.length()));
		}
		return personalFilter.toString();
	}

	private static void appendSecurityKeyValue(final StringBuilder personalFilter, final String key, final Map<String, List<Serializable>> securityKeys) {
		final List<Serializable> securityValue = securityKeys.get(key); //peut etre null, ce qui donnera /null/
		Assertion.check().when(securityValue != null, () -> Assertion.check().isTrue(securityValue.size() == 1, "SecurityKey ({0}) dont support multivalued values yet", key));
		if (securityValue != null) {
			personalFilter.append(securityValue.get(0));
		} else {
			personalFilter.append("null");
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAuthorized(final String resourceType, final Object resource, final String operation) {
		final ResourceNameFactory resourceNameFactory = resourceNameFactories.get(resourceType);
		Assertion.check().isNotNull(resourceNameFactory, "Ce type de resource : {0}, ne possède pas de ResourceNameFactory.", resourceType);
		final String resourceName = resourceNameFactory.toResourceName(resource);
		return isAuthorized(resourceName, operation);
	}

	/** {@inheritDoc} */
	@Override
	public void registerResourceNameFactory(final String resourceType, final ResourceNameFactory resourceNameFactory) {
		Assertion.check().isNotBlank(resourceType);
		Assertion.check().isNotNull(resourceNameFactory);
		//-----
		resourceNameFactories.put(resourceType, resourceNameFactory);
	}
}
