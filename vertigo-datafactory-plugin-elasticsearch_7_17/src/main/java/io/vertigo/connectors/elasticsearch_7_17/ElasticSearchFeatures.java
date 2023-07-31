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
package io.vertigo.connectors.elasticsearch_7_17;

import io.vertigo.core.node.config.Feature;
import io.vertigo.core.node.config.Features;
import io.vertigo.core.param.Param;

/**
 * Defines ElasticSearchConnector module.
 * @author npiedeloup
 */
public final class ElasticSearchFeatures extends Features<ElasticSearchFeatures> {

	/**
	 * Constructor.
	 */
	public ElasticSearchFeatures() {
		super("vertigo-elasticsearch-connector");
	}

	@Feature("embeddedServer")
	public ElasticSearchFeatures withEmbeddedServer(final Param... params) {
		getModuleConfigBuilder()
				.addComponent(EmbeddedElasticSearchServer.class, params);
		return this;
	}

	@Feature("transport")
	public ElasticSearchFeatures withTransport(final Param... params) {
		getModuleConfigBuilder()
				.addConnector(TransportElasticSearchConnector.class, params);
		return this;
	}

	@Feature("node")
	public ElasticSearchFeatures withNode(final Param... params) {
		getModuleConfigBuilder()
				.addConnector(NodeElasticSearchConnector.class, params);
		return this;
	}

	@Feature("restHL")
	public ElasticSearchFeatures withRestHL(final Param... params) {
		getModuleConfigBuilder()
				.addConnector(RestHighLevelElasticSearchConnector.class, params);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	protected void buildFeatures() {
		//
	}
}
