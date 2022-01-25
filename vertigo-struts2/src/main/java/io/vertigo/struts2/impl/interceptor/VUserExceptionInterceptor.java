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
package io.vertigo.struts2.impl.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import io.vertigo.core.lang.VUserException;
import io.vertigo.struts2.core.AbstractActionSupport;
import io.vertigo.vega.webservice.validation.ValidationUserException;

/**
 * Interceptor Struts des exceptions de type UserException pour ajouter les messages à la page et la réafficher.
 * @author npiedeloup
 */
public class VUserExceptionInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = -3416159964166247585L;

	/** {@inheritDoc} */
	@Override
	public String intercept(final ActionInvocation actionInvocation) throws Exception {
		try {
			return actionInvocation.invoke();
		} catch (final ValidationUserException e) {
			final AbstractActionSupport action = (AbstractActionSupport) actionInvocation.getAction();
			e.flushToUiMessageStack(action.getUiMessageStack());
			action.getModel().markDirty();
			return Action.NONE;
		} catch (final VUserException e) {
			final AbstractActionSupport action = (AbstractActionSupport) actionInvocation.getAction();
			action.getUiMessageStack().error(e.getMessage());
			action.getModel().markDirty();
			return Action.NONE;
		}
	}
}
