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
package io.vertigo.account.authorization.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.account.authorization.AuthorizationBasicManager;
import io.vertigo.account.authorization.UserAuthorizationsBasic;
import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.account.data.TestUserSession;
import io.vertigo.account.impl.authorization.xml.BeanResourceNameFactory;
import io.vertigo.account.security.UserSession;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;

/**
 * @author pchretien
 */
public final class VSecurityManagerTest {

	private static final String OP_WRITE = "Write";
	private static final String OP_READ = "Read";
	private static final String R_MY_FAMILLE = "MyFamille";
	private static final String R_ALL_FAMILLES = "AllFamilles";
	private static final String R_WRITER = "Writer";
	private static final String R_READER = "Reader";
	private static final String R_ADMIN = SecurityNames.Roles.RbAdmin.name();
	private static final String R_MANAGER = SecurityNames.Roles.RbManager.name();
	private static final String R_SECRETARY = SecurityNames.Roles.RbSecretary.name();

	@Inject
	private VSecurityManager securityManager;
	@Inject
	private AuthorizationBasicManager authorizationManager;

	private AutoCloseableNode node;

	private UserSession userSession;

	@BeforeEach
	public void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		userSession = securityManager.<TestUserSession> createUserSession();
	}

	@AfterEach
	public void tearDown() {
		userSession = null;
		if (node != null) {
			node.close();
		}
	}

	//non final, to be overrided for previous lib version
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config();
	}

	@Test
	public void testCreateUserSession() {
		assertEquals(Locale.FRANCE, userSession.getLocale());
		assertEquals(TestUserSession.class, userSession.getClass());
	}

	@Test
	public void testInitCurrentUserSession() {
		try {
			securityManager.startCurrentUserSession(userSession);
			assertTrue(securityManager.getCurrentUserSession().isPresent());
			assertEquals(userSession, securityManager.getCurrentUserSession().get());
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	public void testAuthenticate() {
		assertFalse(userSession.isAuthenticated());
		userSession.authenticate();
	}

	@Test
	public void testNoUserSession() {
		final Optional<UserSession> userSessionFromManager = securityManager.getCurrentUserSession();
		assertFalse(userSessionFromManager.isPresent());
	}

	@Test
	public void testResetUserSession() {
		try {
			securityManager.startCurrentUserSession(userSession);
			assertTrue(securityManager.getCurrentUserSession().isPresent());
			//
		} finally {
			securityManager.stopCurrentUserSession();
		}
		assertFalse(securityManager.getCurrentUserSession().isPresent());
	}

	@Test
	public void testRole() {
		final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
		final RoleBasic admin = definitionSpace.resolve(R_ADMIN, RoleBasic.class);
		assertTrue(R_ADMIN.equals(admin.getName()));
		final RoleBasic secretary = definitionSpace.resolve(R_SECRETARY, RoleBasic.class);
		assertTrue(R_SECRETARY.equals(secretary.getName()));
	}

	@Test
	public void testAccess() {
		final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
		final RoleBasic admin = definitionSpace.resolve(R_ADMIN, RoleBasic.class);
		final RoleBasic manager = definitionSpace.resolve(R_MANAGER, RoleBasic.class);
		final RoleBasic secretary = definitionSpace.resolve(R_SECRETARY, RoleBasic.class);

		prepareUserAuthorizations(userAuth -> userAuth
				.addRole(admin)
				.addRole(manager));
		try {
			securityManager.startCurrentUserSession(userSession);

			assertTrue(authorizationManager.hasRole(admin, secretary));

			assertFalse(authorizationManager.hasRole(secretary));

			assertTrue(authorizationManager.hasRole()); //Si aucun droit necessaire alors c'est bon
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	@Test
	public void testNotAuthorized() {
		final RoleBasic reader = getRole(R_READER);
		final RoleBasic writer = getRole(R_WRITER);

		prepareUserAuthorizations(userAuth -> userAuth
				.addRole(reader)
				.addRole(writer));
		try {
			securityManager.startCurrentUserSession(userSession);
			final boolean authorized = authorizationManager.isAuthorized("not", "authorized");
			assertFalse(authorized);
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	@Test
	public void testAuthorized() {
		final RoleBasic reader = getRole(R_READER);
		final RoleBasic writer = getRole(R_WRITER);

		prepareUserAuthorizations(userAuth -> userAuth
				.addRole(reader)
				.addRole(writer));
		try {
			securityManager.startCurrentUserSession(userSession);
			final boolean canread = authorizationManager.isAuthorized("/products/12", OP_READ);
			assertTrue(canread);
			final boolean canwrite = authorizationManager.isAuthorized("/products/12", OP_WRITE);
			assertTrue(canwrite);
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	@Test
	public void testNoWriterRole() {
		final RoleBasic reader = getRole(R_READER);

		prepareUserAuthorizations(userAuth -> userAuth
				.addRole(reader));
		try {
			securityManager.startCurrentUserSession(userSession);
			final boolean canread = authorizationManager.isAuthorized("/products/12", OP_READ);
			assertTrue(canread);
			final boolean cannotwrite = authorizationManager.isAuthorized("/products/12", OP_WRITE);
			assertFalse(cannotwrite);
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	@Test
	public void testAuthorizedAllWithResourceNameFactory() {
		authorizationManager.registerResourceNameFactory(Famille.class.getSimpleName(), new BeanResourceNameFactory("/famille/${famId}"));
		final Famille famille12 = new Famille();
		famille12.setFamId(12L);

		final Famille famille13 = new Famille();
		famille13.setFamId(13L);

		//Test toutes familles
		final RoleBasic readAllFamillies = getRole(R_ALL_FAMILLES);

		prepareUserAuthorizations(userAuth -> userAuth
				.addRole(readAllFamillies));
		try {
			securityManager.startCurrentUserSession(userSession);
			final boolean canRead12 = authorizationManager.isAuthorized(Famille.class.getSimpleName(), famille12, OP_READ);
			assertTrue(canRead12);
			final boolean canRead13 = authorizationManager.isAuthorized(Famille.class.getSimpleName(), famille13, OP_READ);
			assertTrue(canRead13);
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	@Test
	public void testAuthorizedSessionPropertyWithResourceNameFactory() {
		authorizationManager.registerResourceNameFactory(Famille.class.getSimpleName(), new BeanResourceNameFactory("/famille/${famId}"));
		final Famille famille12 = new Famille();
		famille12.setFamId(12L);

		final Famille famille13 = new Famille();
		famille13.setFamId(13L);

		//Test ma famille
		final RoleBasic readMyFamilly = getRole(R_MY_FAMILLE);

		prepareUserAuthorizations(userAuth -> userAuth
				.withSecurityKeys("famId", String.valueOf(famille12.id))
				.addRole(readMyFamilly));
		try {
			securityManager.startCurrentUserSession(userSession);
			final boolean canRead12 = authorizationManager.isAuthorized(Famille.class.getSimpleName(), famille12, OP_READ);
			assertTrue(canRead12);
			final boolean canRead13 = authorizationManager.isAuthorized(Famille.class.getSimpleName(), famille13, OP_READ);
			assertFalse(canRead13);
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	private RoleBasic getRole(final String name) {
		final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
		return definitionSpace.resolve(RoleBasic.PREFIX + name, RoleBasic.class);
	}

	private void prepareUserAuthorizations(final Consumer<UserAuthorizationsBasic> userAuthorizationsFct) {
		try {
			securityManager.startCurrentUserSession(userSession);
			userAuthorizationsFct.accept(authorizationManager.obtainUserAuthorizations());
		} finally {
			securityManager.stopCurrentUserSession();
		}
	}

	public static final class Famille {
		private long id;

		public void setFamId(final long id) {
			this.id = id;
		}

		public long getFamId() {
			return id;
		}
	}
}
