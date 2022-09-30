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
package io.vertigo.struts2.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.function.UnaryOperator;

import javax.inject.Inject;

import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datafactory.collections.CollectionsManager;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.struts2.core.AbstractActionSupport;
import io.vertigo.struts2.core.ContextRef;
import io.vertigo.vega.webservice.model.UiList;

/**
 * Service web de l'autocomplete des listes.
 * @author npiedeloup
 */
public final class ListAutocompleteAction extends AbstractActionSupport {

	private static final long serialVersionUID = -488467479129486060L;

	private final ContextRef<String> termRef = new ContextRef<>("term", String.class, this);
	private final ContextRef<String> listRef = new ContextRef<>("list", String.class, this);
	private final ContextRef<String> listKeyRef = new ContextRef<>("listKey", String.class, this);
	private final ContextRef<String> listValueRef = new ContextRef<>("listValue", String.class, this);
	@Inject
	private CollectionsManager collectionsManager;
	@Inject
	private VTransactionManager transactionManager; //used for search in linked masterdatalist

	/** {@inheritDoc} */
	@Override
	protected void initContext() {
		//
	}

	/**
	 * Ajoute dans la response le json de la recherche.
	 * @param <D> Object Type
	 * @return Outcome de la requete Ajax.
	 */
	public <D extends DtObject> String searchFullText() {
		final String searchString = termRef.get();
		final Object contextList = getModel().get(listRef.get());
		if (!(contextList instanceof UiList)) {
			throw new VUserException("La liste n'est pas du bon type {0}", listRef.get());
		}
		final DtList<D> list = ((UiList<D>) contextList).mergeAndCheckInput(Collections.EMPTY_LIST, getUiMessageStack());
		final DtDefinition dtDefinition = list.getDefinition();
		//-----
		final DtField idField;
		if (listKeyRef.exists()) {
			idField = dtDefinition.getField(listKeyRef.get());
		} else {
			idField = dtDefinition.getIdField().get();
		}
		//-----
		final DtField labelField;
		if (listValueRef.exists()) {
			labelField = dtDefinition.getField(listValueRef.get());
		} else {
			labelField = dtDefinition.getDisplayField().get();
		}

		final Collection<DtField> searchedFields = Collections.singletonList(labelField);
		final DtList<D> results;
		try (final VTransactionWritable transaction = transactionManager.createCurrentTransaction()) { //Open a transaction because all fields are indexed. If there is a MDL it was load too.
			final UnaryOperator<DtList<D>> fullTextFilter = collectionsManager.<D> createIndexDtListFunctionBuilder()
					.filter(searchString != null ? searchString : "", 20, searchedFields)
					.build();
			results = fullTextFilter.apply(list);
		}
		return createAjaxResponseBuilder()
				.withJson(toJson(results, idField, labelField))
				.send();
	}

	private static String toJson(final DtList<?> dtList, final DtField keyField, final DtField labelField) {
		final StringBuilder sb = new StringBuilder("[");
		String sep = "";
		for (final DtObject dto : dtList) {
			final Object keyValue = keyField.getDataAccessor().getValue(dto);
			final String labelValue = (String) labelField.getDataAccessor().getValue(dto);
			final String labelEncoded = jsonEncode(labelValue);
			sb.append(sep)
					.append("{\"key\":")
					.append('\"')
					.append(keyValue)
					.append("\",\"value\":")
					.append('\"')
					.append(labelEncoded)
					.append("\"}");
			sep = ", ";
		}
		sb.append(']');
		return sb.toString();
	}

	private static String jsonEncode(final String json) {
		return json
				.replaceAll("([\"\\\\])", "\\\\$1")// " => \" et \ => \\ (ils sont echappés avec \ devant)
				.replaceAll("\n", "|") // \n => | (interdit en json)
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
