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

import java.io.File;
import java.io.Serializable;

import io.vertigo.core.lang.Assertion;

/**
 * Liste des couples (clé, object) enregistrés.
 * @author npiedeloup
 * @param <O> Type d'objet
 */
public final class ContextRef<O extends Serializable> {
	private final AbstractActionSupport action;
	private final String contextKey;
	private final Class<O> valueClass;

	/**
	 * Constructeur.
	 * @param contextKey Clé dans le context
	 * @param valueClass Type du paramètre
	 * @param action Action struts
	 */
	public ContextRef(final String contextKey, final Class<O> valueClass, final AbstractActionSupport action) {
		Assertion.check()
				.isNotBlank(contextKey)
				.isNotNull(action)
				.isNotNull(valueClass)
				.isTrue(
						String[].class.equals(valueClass) ||
								String.class.equals(valueClass) ||
								Long.class.equals(valueClass) ||
								Integer.class.equals(valueClass) ||
								Boolean.class.equals(valueClass) ||
								File.class.equals(valueClass),
						"Le type du paramètre doit être un type primitif (String, Long, Integer, Boolean ou String[]) ou de type File ici {0}.", valueClass.getName());
		//-----
		this.contextKey = contextKey;
		this.action = action;
		this.valueClass = valueClass;
	}

	/**
	 * @param value Valeur à mettre dans le context
	 */
	public void set(final O value) {
		Assertion.check()
				.isNotNull(value, "la valeur doit être renseignée pour {0}", contextKey)
				.isTrue(valueClass.isInstance(value), "Cette valeur n'est pas du bon type ({0} au lieu de {1})", value.getClass(), valueClass);
		//----
		action.getModel().put(contextKey, value);
	}

	/**
	 * @return Object du context
	 */
	public O get() {
		final Serializable value = action.getModel().get(contextKey);
		if (value instanceof String[] && !String[].class.equals(valueClass)) { //cas ou la valeur a été settée depuis la request
			final String firstValue = ((String[]) value).length > 0 ? ((String[]) value)[0] : null;
			if (firstValue == null || firstValue.isEmpty()) { //depuis la request : empty == null
				return null;
			} else if (String.class.equals(valueClass)) {
				return valueClass.cast(firstValue);
			} else if (Long.class.equals(valueClass)) {
				return valueClass.cast(Long.valueOf(firstValue));
			} else if (Integer.class.equals(valueClass)) {
				return valueClass.cast(Integer.valueOf(firstValue));
			} else if (Boolean.class.equals(valueClass)) {
				return valueClass.cast(Boolean.valueOf(firstValue));
			}
		}
		if (value instanceof File[]) {
			//TODO revoir la gestion des fichiers
			return valueClass.cast(((File[]) value)[0]);
		}
		return valueClass.cast(value);
	}

	/**
	 * @return Si cet élément est dans le context.
	 */
	public boolean exists() {
		return action.getModel().containsKey(contextKey);
	}

	/**
	 * Vide la valeur de ce champ du contexte
	 */
	public void clear() {
		action.getModel().remove(contextKey);
	}

}
