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
package io.vertigo.struts2.core;

import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.DtListURIForMasterData;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * Liste de reference des couples (clé, object) enregistrés.
 * @author npiedeloup
 * @param <O> Type d'objet
 */
public final class ContextMdl<O extends Entity> {
	private final AbstractActionSupport action;
	private final String contextKey;

	/**
	 * Constructeur.
	 * @param contextKey Clé dans le context
	 * @param action Action struts
	 */
	public ContextMdl(final String contextKey, final AbstractActionSupport action) {
		Assertion.check()
				.isNotBlank(contextKey)
				.isNotNull(action);
		//-----
		this.contextKey = contextKey;
		this.action = action;
	}

	/**
	 * Publie une liste de référence.
	 * @param dtObjectClass Class associée
	 * @param code Code
	 */
	public void publish(final Class<O> dtObjectClass, final String code) {
		final DtDefinition dtDefinition = DtObjectUtil.findDtDefinition(dtObjectClass);
		action.getModel().put(contextKey, new UiMdList<O>(new DtListURIForMasterData(dtDefinition, code)));
	}
}
