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
package io.vertigo.struts2.ui.controller;

import org.apache.struts2.ServletActionContext;

import io.vertigo.struts2.core.AbstractActionSupport;
import io.vertigo.struts2.impl.MethodUtil;
import io.vertigo.struts2.impl.servlet.RequestContainerWrapper;

/**
 * Super class des Actions struts Test.
 *
 * @author npiedeloup
 * @version $Id: AbstractDemoActionSupport.java,v 1.2 2013/11/18 10:26:13 npiedeloup Exp $
 */
public abstract class AbstractTestActionSupport extends AbstractActionSupport {

	private static final long serialVersionUID = 374760712087148984L;

	/**
	* Constructeur.
	*/
	protected AbstractTestActionSupport() {
		//rien
	}

	/** {@inheritDoc} */
	@Override
	protected void initContext() {
		final RequestContainerWrapper initContainer = new RequestContainerWrapper(ServletActionContext.getRequest());
		MethodUtil.invoke(this, "initContext", initContainer);
	}

	/**
	 * Retourne le nom de la page.
	 * @return Nom de la page.
	 */
	public abstract String getPageName();
}
