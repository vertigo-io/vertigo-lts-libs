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
package io.vertigo.struts2.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Container;
import io.vertigo.core.param.ParamValue;
import io.vertigo.core.util.ClassUtil;

/**
 * Gestion du passage de paramètres aux Actions.
 * @author npiedeloup
 */
public final class MethodUtil {

	private MethodUtil() {
		//privé pour une classe utilitaire
	}

	/**
	 * Invocation dynamique d'une méthode sur une instance.
	 *
	 * @param instance Objet sur lequel est invoqu� la méthode
	 * @param methodName Nom de la methode invoqu�e (la premiere trouvée est appelée)
	 * @param container Container des arguments
	 * @return R Valeur retournée par l'invocation
	 */
	public static Object invoke(final Object instance, final String methodName, final Container container) {
		final Optional<Method> actionMethodOpt = findMethodByName(instance.getClass(), methodName);
		if (actionMethodOpt.isEmpty()) {
			throw new VSystemException("Méthode {0} non trouvée sur {1}", methodName, instance.getClass().getName());
		}
		actionMethodOpt.get().setAccessible(true); //la méthode peut être protected
		return invoke(instance, actionMethodOpt.get(), container);
	}

	/**
	 * Invocation dynamique d'une méthode sur une instance.
	 *
	 * @param instance Objet sur lequel est invoqu� la méthode
	 * @param method Methode invoqu�e
	 * @param container Container des arguments
	 * @return R Valeur retournée par l'invocation
	 */
	public static Object invoke(final Object instance, final Method method, final Container container) {
		Assertion.check()
				.isNotNull(instance)
				.isNotNull(method);
		//-----
		final Object[] args = findMethodParameters(container, method);
		return ClassUtil.invoke(instance, method, args);
	}

	/**
	 * Retrouve une méthode par son nom.
	 * Part de la class d�clarante et remonte les superclass.
	 * @param declaringClass Class de la méthode
	 * @param methodName Nom de la méthode
	 * @return Option de la première méthode trouvée.
	 */
	public static Optional<Method> findMethodByName(final Class<?> declaringClass, final String methodName) {
		for (final Method method : declaringClass.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				return Optional.of(method);
			}
		}
		if (declaringClass.getSuperclass() != null) {
			return findMethodByName(declaringClass.getSuperclass(), methodName);
		}
		return Optional.empty();
	}

	private static Object[] findMethodParameters(final Container container, final Method method) {
		final Object[] parameters = new Object[method.getParameterTypes().length];
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			parameters[i] = getInjected(container, method, i);
		}
		return parameters;
	}

	//On récupère pour le paramètre i du constructeur l'objet à injecter
	private static Object getInjected(final Container container, final Method method, final int i) {
		final String id = getNamedValue(method.getParameterAnnotations()[i]);
		//-----
		final boolean optionalParameter = isOptional(method, i);
		if (optionalParameter) {
			if (container.contains(id)) {
				return Optional.of(container.resolve(id, ClassUtil.getGeneric(method, i)));
			}
			return Optional.empty();
		}
		final Object value = container.resolve(id, method.getParameterTypes()[i]);
		Assertion.check().isNotNull(value);
		//-----
		return value;
	}

	private static boolean isOptional(final Method method, final int i) {
		Assertion.check().isNotNull(method);
		//-----
		return Optional.class.isAssignableFrom(method.getParameterTypes()[i]);
	}

	private static String getNamedValue(final Annotation[] annotations) {
		for (final Annotation annotation : annotations) {
			if (annotation instanceof ParamValue) {
				return ParamValue.class.cast(annotation).value();
			}
		}
		return null;
	}
}
