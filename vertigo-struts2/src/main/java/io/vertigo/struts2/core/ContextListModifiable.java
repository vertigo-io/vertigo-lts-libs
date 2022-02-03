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
package io.vertigo.struts2.core;

import java.util.Collections;

import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.vega.webservice.validation.DefaultDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectValidator;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import io.vertigo.vega.webservice.validation.ValidationUserException;

/**
 * Liste des couples (clé, object) enregistrés.
 * @author npiedeloup
 * @param <O> Type d'objet
 */
public final class ContextListModifiable<O extends DtObject> {
	private final AbstractActionSupport action;
	private final UiMessageStack uiMessageStack;
	private final String contextKey;
	private final DtObjectValidator<O> validator;

	/**
	 * Constructeur.
	 * @param contextKey Clé dans le context
	 * @param action Action struts
	 */
	public ContextListModifiable(final String contextKey, final AbstractActionSupport action) {
		this(contextKey, new DefaultDtObjectValidator<O>(), action);
	}

	/**
	 * Constructeur.
	 * @param contextKey Clé dans le context
	 * @param validator Validator a utiliser
	 * @param action Action struts
	 */
	public ContextListModifiable(final String contextKey, final DtObjectValidator<O> validator, final AbstractActionSupport action) {
		Assertion.check()
				.isNotBlank(contextKey)
				.isNotNull(action)
				.isNotNull(validator);
		//-----
		this.contextKey = contextKey;
		this.action = action;
		this.uiMessageStack = action.getUiMessageStack();
		this.validator = validator;
	}

	/**
	 * Ajoute une liste au context.
	 * @param dtList List à publier
	 */
	public void publish(final DtList<O> dtList) {
		action.getModel().put(contextKey, new StrutsUiListModifiable<>(dtList, contextKey));
	}

	/**
	 * Vérifie les erreurs de la liste. Celles-ci sont ajoutées à l'uiMessageStack si nécessaire.
	 */
	public void checkErrors() {
		getUiListModifiable().checkFormat(uiMessageStack);
		if (uiMessageStack.hasErrors()) {
			throw new ValidationUserException();
		}
	}

	/**
	 * @return List des objets métiers validée. Lance une exception si erreur.
	 */
	public DtList<O> readDtList() {
		checkErrors();
		final DtList<O> validatedList = getUiListModifiable().mergeAndCheckInput(Collections.singletonList(validator), uiMessageStack);
		if (uiMessageStack.hasErrors()) {
			throw new ValidationUserException();
		}
		return validatedList;
	}

	/**
	 * @return List des objets d'IHM. Peut contenir des erreurs.
	 */
	public StrutsUiListModifiable<O> getUiListModifiable() {
		return action.getModel().<O> getUiListModifiable(contextKey);
	}
}
