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
package io.vertigo.account.authorization;

import io.vertigo.account.authorization.definitions.RoleBasic;
import io.vertigo.core.node.component.Manager;

/**
 * Gestion centralisee des droits d'acces.
 *
 * @author npiedeloup
 */
public interface AuthorizationBasicManager extends Manager {
	//=========================================================================
	//===============Gestion de l'utilisateur (porteur des droits)=============
	//=========================================================================

	/**
	 * User authorization accessor to test or add authorizations.
	 * A UserSession must exists.
	 * @return UserAuthorizations
	 */
	UserAuthorizationsBasic obtainUserAuthorizations();

	/**
	 * Contrôle d'accès basé sur les rôles.
	 *
	 * L'utilisateur dispose-t-il des droits nécessaires.
	 * <br/>
	 * <ul>
	 * <li>Si la liste des rôles autorisés est vide, on considère que l'objet n'est pas soumis à autorisation et donc l'accès est accordé.</li>
	 * <li>Si la liste contient au moins un élément alors l'objet est sécurisé et il est nécessaire que
	 * l'utilisateur dispose d'au moins un des rôles autorisés pour que l'accès soit accordé.</li>
	 * </ul>
	 *
	 * La fonction d'accès autorise la session utilisateur <code>null</code> : il faut alors que la liste des droits soit vide.
	 *
	 * @param authorizedRoleSet Set des roles autorisés. (non null)
	 *
	 * @return Si les droits de l'utilisateur lui permettent un accès.
	 */
	boolean hasRole(RoleBasic... authorizedRoleSet);

	/**
	 * Controle d'acces base sur les permissions.
	 *
	 * Indique si l'utilisateur courant a la permission d'effectuer l'operation
	 * donnee sur la ressource donnee.
	 *
	 * @param resource la ressource
	 * @param operation l'operation
	 * @return true si l'utilisateur courant a la permission d'effectuer l'operation
	 * donnée sur la ressource donnee
	 */
	boolean isAuthorized(String resource, String operation);

	/**
	 * Contrôle d'accès basé sur les permissions.
	 *
	 * Indique si l'utilisateur courant a la permission d'effectuer l'opération
	 * donnée sur la ressource donnée.
	 * @param resourceType Type de la resource
	 * @param resource la ressource
	 * @param operation l'opération
	 * @return true si l'utilisateur courant a la permission d'effectuer l'opération
	 * donnée sur la ressource donnée
	 */
	boolean isAuthorized(String resourceType, Object resource, String operation);

	/**
	 * Enregistre une ResourceNameFactory spécifique pour un type donnée.
	 * @param resourceType Type de la resource
	 * @param resourceNameFactory ResourceNameFactory spécifique
	 */
	void registerResourceNameFactory(final String resourceType, final ResourceNameFactory resourceNameFactory);
}
