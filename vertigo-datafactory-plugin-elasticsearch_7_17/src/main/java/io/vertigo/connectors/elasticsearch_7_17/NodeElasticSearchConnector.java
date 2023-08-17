/*
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

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.param.ParamValue;

/**
 * Gestion de la connexion au serveur elasticSearch en mode HTTP.
 *
 * @author npiedeloup
 */
public class NodeElasticSearchConnector implements ElasticSearchConnector {

	private final String connectorName;
	/** url du serveur elasticSearch. */
	private final String[] serversNames;
	/** cluster à rejoindre. */
	private final String clusterName;
	/** Nom du node. */
	private final String nodeName;
	/** Started node. */
	private Node node;

	/**
	 * Constructor.
	 *
	 * @param serversNamesStr URL du serveur ElasticSearch avec le port de communication de cluster (9300 en général)
	 * @param envIndex Nom de l'index de l'environment
	 * @param envIndexIsPrefix Si Nom de l'index de l'environment est un prefix
	 * @param rowsPerQuery Liste des indexes
	 * @param clusterName : nom du cluster à rejoindre
	 * @param configFile fichier de configuration des index
	 * @param nodeNameOpt : nom du node
	 */
	@Inject
	public NodeElasticSearchConnector(
			@ParamValue("name") final Optional<String> connectorNameOpt,
			@ParamValue("servers.names") final String serversNamesStr,
			@ParamValue("cluster.name") final String clusterName,
			@ParamValue("node.name") final Optional<String> nodeNameOpt) {
		Assertion.check()
				.isNotBlank(serversNamesStr,
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isFalse(serversNamesStr.contains(";"),
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isNotBlank(clusterName,
						"Cluster's name must be defined")
				.isFalse("elasticsearch".equals(clusterName),
						"You have to define a cluster name different from the default one");
		// ---------------------------------------------------------------------
		connectorName = connectorNameOpt.orElse("main");
		serversNames = serversNamesStr.split(",");
		this.clusterName = clusterName;
		nodeName = nodeNameOpt.orElseGet(() -> "es-client-node-" + System.currentTimeMillis());
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		final Environment nodeEnvironment = InternalSettingsPreparer.prepareEnvironment(buildNodeSettings(), Collections.emptyMap(), null, null);
		node = new Node(nodeEnvironment);
		try {
			node.start();
		} catch (final NodeValidationException e) {
			throw WrappedException.wrap(e, "Error at ElasticSearch node start");
		}
	}

	@Override
	public String getName() {
		return connectorName;
	}

	@Override
	public Client getClient() {
		return node.client();
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		try {
			node.close();
		} catch (final IOException e) {
			throw WrappedException.wrap(e, "Error at ElasticSearch node stop");
		}
	}

	protected Settings buildNodeSettings() {
		// Build settings
		return Settings.builder().put("node.name", nodeName)
				.put("node.data", false)
				.put("node.master", false)
				.put("node.ingest", false)
				.put("cluster.remote.connect", false)
				.putList("discovery.zen.ping.unicast.hosts", serversNames)
				.put("cluster.name", clusterName)
				.build();
	}
}
