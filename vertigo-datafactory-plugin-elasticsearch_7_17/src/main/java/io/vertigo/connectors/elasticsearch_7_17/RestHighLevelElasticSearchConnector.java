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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Connector;
import io.vertigo.core.param.ParamValue;

/**
 * Gestion de la connexion au serveur elasticSearch en mode HTTP.
 *
 * @author npiedeloup
 */
public class RestHighLevelElasticSearchConnector implements Connector<RestHighLevelClient>, Activeable {

	private final String connectorName;
	/** url du serveur elasticSearch. */
	private final String[] serversNames;
	/** le noeud interne. */
	private RestHighLevelClient client;

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
	public RestHighLevelElasticSearchConnector(
			@ParamValue("name") final Optional<String> connectorNameOpt,
			@ParamValue("servers.names") final String serversNamesStr) {
		Assertion.check()
				.isNotBlank(serversNamesStr,
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isFalse(serversNamesStr.contains(";"),
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','");
		// ---------------------------------------------------------------------
		connectorName = connectorNameOpt.orElse("main");
		serversNames = serversNamesStr.split(",");
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		client = new RestHighLevelClient(buildRestClientBuilder());
	}

	@Override
	public String getName() {
		return connectorName;
	}

	@Override
	public RestHighLevelClient getClient() {
		return client;
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		try {
			client.close();
		} catch (final IOException e) {
			WrappedException.wrap(e);
		}
	}

	protected RestClientBuilder buildRestClientBuilder() {
		final List<HttpHost> httpHostList = new ArrayList<>();
		for (final String serverName : serversNames) {
			final String[] serverNameSplit = serverName.split(":");
			Assertion.check().isTrue(serverNameSplit.length == 2,
					"La déclaration du serveur doit être au format host:port ({0})", serverName);
			final int port = Integer.parseInt(serverNameSplit[1]);
			httpHostList.add(new HttpHost(serverNameSplit[0], port));
		}

		final HttpHost[] httpHosts = httpHostList.toArray(new HttpHost[httpHostList.size()]);
		return RestClient.builder(httpHosts);
	}
}
