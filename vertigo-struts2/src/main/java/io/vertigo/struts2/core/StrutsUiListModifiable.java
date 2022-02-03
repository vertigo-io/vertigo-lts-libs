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

import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.vega.engines.webservice.json.AbstractUiListModifiable;
import io.vertigo.vega.webservice.model.UiObject;

public class StrutsUiListModifiable<D extends DtObject> extends AbstractUiListModifiable<D> {

	private static final long serialVersionUID = -6612061761970992295L;

	StrutsUiListModifiable(final DtList<D> dtList, final String inputKey) {
		super(dtList, inputKey);

	}

	/* (non-Javadoc)
	 * @see io.vertigo.struts2.core.AbstractUiListModifiable#createUiObject(io.vertigo.dynamo.domain.model.DtObject)
	 */
	@Override
	protected UiObject<D> createUiObject(final D dto) {
		return new StrutsUiObject<>(dto);
	}

	@Override
	protected String toContextKey(final String inputKey, final int index) {
		return inputKey + ".get(" + index + ")";
	}

}
