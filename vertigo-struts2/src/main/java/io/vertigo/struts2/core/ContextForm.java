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

import java.util.Collections;

import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.vega.webservice.model.UiObject;
import io.vertigo.vega.webservice.validation.DefaultDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectValidator;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import io.vertigo.vega.webservice.validation.ValidationUserException;

/**
 * Liste des couples (clé, object) enregistrés.
 * @author npiedeloup
 * @param <O> Type d'objet
 */
public final class ContextForm<O extends DtObject> {
	private final AbstractActionSupport action;
	private final UiMessageStack uiMessageStack;
	private final String contextKey;
	private final DtObjectValidator<O> validator;

	/**
	 * Constructeur.
	 * @param contextKey Clé dans le context
	 * @param action Action struts
	 */
	public ContextForm(final String contextKey, final AbstractActionSupport action) {
		this(contextKey, new DefaultDtObjectValidator<O>(), action);
	}

	/**
	 * Constructeur.
	 * @param contextKey Clé dans le context
	 * @param validator Validator a utiliser
	 * @param action Action struts
	 */
	public ContextForm(final String contextKey, final DtObjectValidator<O> validator, final AbstractActionSupport action) {
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
	 * Ajoute un objet de type form au context.
	 * @param dto Objet à publier
	 */
	public void publish(final O dto) {
		final UiObject<O> strutsUiObject = new StrutsUiObject<>(dto);
		strutsUiObject.setInputKey(contextKey);
		action.getModel().put(contextKey, strutsUiObject);
	}

	/**
	 * Vérifie les erreurs de l'objet. Celles-ci sont ajoutées à l'uiMessageStack si nécessaire.
	 */
	public void checkErrors() {
		getUiObject().checkFormat(uiMessageStack);
		if (uiMessageStack.hasErrors()) {
			throw new ValidationUserException();
		}
	}

	/**
	 * @return objet métier valid�. Lance une exception si erreur.
	 */
	public O readDto() {
		checkErrors();
		// ---
		final O validatedDto = getUiObject().mergeAndCheckInput(Collections.singletonList(validator), uiMessageStack);
		if (uiMessageStack.hasErrors()) {
			throw new ValidationUserException();
		}
		return validatedDto;
	}

	/**
	 * @return Objet d'IHM. Peut contenir des erreurs.
	 */
	public UiObject<O> getUiObject() {
		return action.getModel().<O> getUiObject(contextKey);
	}
}
