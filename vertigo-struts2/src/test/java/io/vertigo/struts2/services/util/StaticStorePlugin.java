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
package io.vertigo.struts2.services.util;

import javax.inject.Inject;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.BasicType;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.param.ParamValue;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.plugins.entitystore.AbstractStaticEntityStorePlugin;

/**
 * MasterDataStore for the static lists.
 * @author npiedeloup
 * @version $Id: TutoMasterDataStoreStatic.java,v 1.3 2014/06/27 12:21:39 pchretien Exp $
 */
public final class StaticStorePlugin extends AbstractStaticEntityStorePlugin implements Activeable {
	private static final String DEFAULT_CONNECTION_NAME = "main";

	private final String values;
	private final String dtDefinitionName;

	private final String dataSpace;
	private DtDefinition staticDtDefinition;
	private DtField idField;
	private DtField displayField;
	private DtList<Entity> dtc;

	/**
	 * A simpler storePlugin for static list.
	 * @param values comma separated list of key=value
	 * @param dtDefinitionName Definition of element
	 */
	@Inject
	public StaticStorePlugin(@ParamValue("values") final String values, @ParamValue("dtDefinitionName") final String dtDefinitionName, @ParamValue("dataSpace") final String dataSpace) {
		super();
		Assertion.check()
				.isNotNull(dtDefinitionName)
				.isNotBlank(values)
				.isTrue(values.contains("="), "StaticStorePlugin takes a list of key value like : key1=Label1;key2=Label2;...");
		//----
		this.dtDefinitionName = dtDefinitionName;
		this.values = values;
		this.dataSpace = dataSpace;
	}

	@Override
	public void start() {
		staticDtDefinition = Node.getNode().getDefinitionSpace().resolve(dtDefinitionName, DtDefinition.class);
		Assertion.check()
				.isTrue(staticDtDefinition.getIdField().isPresent(), "The Static MasterDataList {0} must have a IdField", staticDtDefinition.getClassSimpleName())
				.isTrue(staticDtDefinition.getDisplayField().isPresent(), "The Static MasterDataList {0} must have a DisplayField", staticDtDefinition.getClassSimpleName());
		idField = staticDtDefinition.getIdField().get();
		displayField = staticDtDefinition.getDisplayField().get();
		//trop tard pour récupèrer le dataSpace, il a déjà été référencé dans le LogicalEntityStoreConfig
		//dataSpace = staticDtDefinition.getDataSpace();
		//----
		dtc = new DtList<>(staticDtDefinition);
		final BasicType keyDataType = staticDtDefinition.getIdField().get().getSmartTypeDefinition().getBasicType();
		final String[] splittedValues = values.split("\\s*[,;|]\\s*");
		for (final String splittedValue : splittedValues) {
			final String[] keyLabel = splittedValue.split("\\s*=\\s*");
			final Entity dto = createDtObject(castToType(keyLabel[0], keyDataType), keyLabel[1]);
			dtc.add(dto);
		}

	}

	@Override
	public void stop() {
		//nothing

	}

	private static Object castToType(final String key, final BasicType keyDataType) {
		switch (keyDataType) {
			case Boolean:
				return Boolean.parseBoolean(key);
			case Integer:
				return Integer.valueOf(key);
			case Long:
				return Long.valueOf(key);
			case String:
				return key;
			default:
				throw new IllegalArgumentException("Not supported type : " + keyDataType.name());
		}
	}

	private Entity createDtObject(final Object key, final String display) {
		final Entity dto = Entity.class.cast(DtObjectUtil.createDtObject(staticDtDefinition));
		staticDtDefinition.getDisplayField().get();
		idField.getDataAccessor().setValue(dto, key);
		displayField.getDataAccessor().setValue(dto, display);
		return dto;
	}

	/** {@inheritDoc} */
	@Override
	public String getDataSpace() {
		return dataSpace;
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionName() {
		return DEFAULT_CONNECTION_NAME;
	}

	/** {@inheritDoc} */
	@Override
	public <E extends Entity> E readNullable(final DtDefinition dtDefinition, final UID<E> uri) {
		Assertion.check().isTrue(dtDefinition.equals(staticDtDefinition), "This store should be use for {0} only, not {1}", staticDtDefinition.getClassSimpleName(), dtDefinition.getClassSimpleName());
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public <E extends Entity> E readNullableForUpdate(final DtDefinition dtDefinition, final UID<?> uri) {
		Assertion.check().isTrue(dtDefinition.equals(staticDtDefinition), "This store should be use for {0} only, not {1}", staticDtDefinition.getClassSimpleName(), dtDefinition.getClassSimpleName());
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public <E extends Entity> DtList<E> findByCriteria(final DtDefinition dtDefinition, final Criteria<E> criteria, final DtListState dtListState) {
		Assertion.check()
				.isNotNull(dtDefinition)
				.isNotNull(dtListState)
				.isTrue(dtDefinition.equals(staticDtDefinition), "This store should be use for {0} only, not {1}", staticDtDefinition.getClassSimpleName(), dtDefinition.getClassSimpleName())
				.isTrue(criteria == null || criteria == Criterions.alwaysTrue(), "This store could only load all data, not {0}", criteria);
		//----
		return (DtList) dtc;
	}

	/** {@inheritDoc} */
	@Override
	public int count(final DtDefinition dtDefinition) {
		return dtc.size();
	}

}
