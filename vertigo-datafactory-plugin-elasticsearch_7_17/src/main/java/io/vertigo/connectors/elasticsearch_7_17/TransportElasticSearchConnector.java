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

import java.net.InetSocketAddress;
import java.util.Optional;

import javax.inject.Inject;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.param.ParamValue;

/**
 * Gestion de la connexion au serveur elasticSearch en mode HTTP.
 *
 * @author npiedeloup
 */
public class TransportElasticSearchConnector implements ElasticSearchConnector {

	private final String connectorName;
	/** url du serveur elasticSearch. */
	private final String[] serversNames;
	/** cluster à rejoindre. */
	private final String clusterName;
	/** Nom du node. */
	private final String nodeName;
	/** le noeud interne. */
	private TransportClient client;

	/**
	 * Constructor.
	 *
	 * @param serversNamesStr URL du serveur ElasticSearch avec le port de communication de cluster (9300 en général)
	 * @param clusterName : nom du cluster à rejoindre
	 * @param nodeNameOpt : nom du node
	 */
	@Inject
	public TransportElasticSearchConnector(
			@ParamValue("name") final Optional<String> connectorNameOpt,
			@ParamValue("servers.names") final String serversNamesStr,
			@ParamValue("cluster.name") final String clusterName,
			@ParamValue("node.name") final Optional<String> nodeNameOpt) {
		Assertion.check()
				.isNotBlank(serversNamesStr,
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isFalse(serversNamesStr.contains(","),
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isNotBlank(clusterName,
						"Cluster's name must be defined")
				.isFalse("elasticsearch".equals(clusterName), "You must define a cluster name different from the default one");
		// ---------------------------------------------------------------------
		connectorName = connectorNameOpt.orElse("main");
		serversNames = serversNamesStr.split(",");
		this.clusterName = clusterName;
		nodeName = nodeNameOpt.orElseGet(() -> "es-client-transport-" + System.currentTimeMillis());
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		client = new PreBuiltTransportClient(buildNodeSettings());
		for (final String serverName : serversNames) {
			final String[] serverNameSplit = serverName.split(":");
			Assertion.check().isTrue(serverNameSplit.length == 2,
					"La déclaration du serveur doit être au format host:port ({0}", serverName);
			final int port = Integer.parseInt(serverNameSplit[1]);
			client.addTransportAddress(new TransportAddress(new InetSocketAddress(serverNameSplit[0], port)));
		}
	}

	@Override
	public String getName() {
		return connectorName;
	}

	@Override
	public Client getClient() {
		return client;
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		client.close();
	}

	protected Settings buildNodeSettings() {
		// Build settings
		return Settings.builder().put("node.name", nodeName)
				// .put("client.transport.sniff", false)
				// .put("client.transport.ignore_cluster_name", false)
				// .put("client.transport.ping_timeout", "5s")
				// .put("client.transport.nodes_sampler_interval", "5s")
				.put("cluster.name", clusterName)
				.build();
	}
}
