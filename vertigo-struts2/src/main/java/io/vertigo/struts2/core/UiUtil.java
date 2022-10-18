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

import java.io.Serializable;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;

import io.vertigo.basics.formatter.FormatterDefault;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.BasicType;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.definitions.Formatter;
import io.vertigo.vega.webservice.model.UiList;
import io.vertigo.vega.webservice.model.UiObject;

/**
 * Class utilitaire pour le rendu des pages en jsp/ftl.
 * @author npiedeloup
 */
public final class UiUtil implements Serializable {

	private static final long serialVersionUID = -5677843485950859547L;
	private static final Formatter DEFAULT_FORMATTER = new FormatterDefault(null);//by convention : no args

	/**
	 * Constructor.
	 */
	//can't be private, because an instance must be put into struts context, for access from tags.
	UiUtil() {
		//empty
	}

	/**
	 * @param uiObject Object du context
	 * @return Nom de l'object dans le context
	 */
	public String contextKey(final UiObject<?> uiObject) {
		final ActionContext actionContext = ActionContext.getContext();
		final KActionContext kActionContext = ((AbstractActionSupport) actionContext.getActionInvocation().getAction()).getModel();
		return kActionContext.findKey(uiObject);
	}

	/**
	 * @param uiList List du context
	 * @param uiObject Objet de la liste
	 * @return index de l'objet dans sa liste
	 */
	public int indexOf(final List<?> uiList, final UiObject<?> uiObject) {
		return uiList.indexOf(uiObject);
	}

	/**
	 * @param fieldPath Chemin du champ
	 * @return Label du champs
	 */
	public String label(final String fieldPath) {
		return getDtField(fieldPath).getLabel().getDisplay();
	}

	/**
	 * @param fieldPath Chemin du champ boolean
	 * @param value Valeur à formater
	 * @return rendu du champs boolean
	 */
	public String formatBoolean(final String fieldPath, final Boolean value) {
		if (!fieldPath.contains(".")) { //cas des ContextRef sans smartType
			return DEFAULT_FORMATTER.valueToString(value, BasicType.Boolean);
		}
		final SmartTypeManager smartTypeManager = Node.getNode().getComponentSpace().resolve(SmartTypeManager.class);
		return smartTypeManager.valueToString(getDtField(fieldPath).smartTypeDefinition(), value);
	}

	/**
	 * @param fieldPath Chemin du champ
	 * @return Si le champs est obligatoire
	 */
	public boolean required(final String fieldPath) {
		Assertion.check().isTrue(fieldPath.indexOf('.') != 0, "FieldPath shouldn't starts with . ({0})", fieldPath);
		//-----
		if (fieldPath.indexOf('.') > 0) { //Le champs est porté par un Object
			return getDtField(fieldPath).cardinality().hasOne();
		}
		return false; //on ne sait pas dire, mais on ne force pas à obligatoire
	}

	/**
	 * @param uiList liste du context
	 * @return Nom du champ display de cette liste
	 */
	public String getDisplayField(final UiList<?> uiList) {
		final DtDefinition dtDefinition = uiList.getDtDefinition();
		return dtDefinition.getDisplayField().get().name();
	}

	/**
	 * @param uiList liste du context
	 * @return Nom du champ de l'id de cette liste
	 */
	public String getIdField(final UiList<?> uiList) {
		final DtDefinition dtDefinition = uiList.getDtDefinition();
		return dtDefinition.getIdField().get().name();
	}

	private static DtField getDtField(final String fieldPath) {
		Assertion.check().isTrue(fieldPath.indexOf('.') > 0, "Le champs n'est pas porté par un Object ({0})", fieldPath);
		//Assertion.check().argument(fieldPath.indexOf('.') == fieldPath.lastIndexOf('.'), "Seul un point est autorisé ({0})", fieldPath);
		final String contextKey = fieldPath.substring(0, fieldPath.lastIndexOf('.'));
		final String fieldName = fieldPath.substring(fieldPath.lastIndexOf('.') + 1);
		final ActionContext actionContext = ActionContext.getContext();
		final Object contextObject = actionContext.getValueStack().findValue(contextKey);
		Assertion.check()
				.isNotNull(contextObject, "{0} n''est pas dans le context", contextKey)
				.isTrue(contextObject instanceof UiObject || contextObject instanceof UiList, "{0}({1}) doit être un UiObject ou une UiList ", contextKey,
						contextObject.getClass().getSimpleName());

		final DtDefinition dtDefinition;
		if (contextObject instanceof UiObject) {
			dtDefinition = ((UiObject<?>) contextObject).getDtDefinition();
		} else {
			dtDefinition = ((UiList<?>) contextObject).getDtDefinition();
		}
		Assertion.check()
				.isNotNull(dtDefinition) //, "{0}({1}) doit être un UiObject ou un UiList ", contextKey, contextObject.getClass().getSimpleName());
				.isNotNull(dtDefinition, "{0}({1}) doit être un UiObject ou un UiList ", contextKey, contextObject.getClass().getSimpleName());
		return dtDefinition.getField(fieldName);

	}
}
