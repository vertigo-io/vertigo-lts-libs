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

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

/**
 * Utilitaire d'accès à la Request.
 * @author npiedeloup
 */
public final class UiRequestUtil {

	private UiRequestUtil() {
		//rien
	}

	/**
	 * Invalide la session Http (ie Logout)
	 */
	public static void invalidateHttpSession() {
		final HttpSession session = ServletActionContext.getRequest().getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

}
