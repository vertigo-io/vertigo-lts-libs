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
