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
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.param.ParamValue;

/**
 * Gestion de la connexion au serveur elasticSearch en mode TCP + SSL.
 * Utilisation du client Transport simple, sans intégration au cluster
 * (permet de ne pas avoir de liaison bi-directionelle entre le tomcat et l'ES externe).
 *
 * @author skerdudou
 */
public class SecuredTransportSearchConnector implements ElasticSearchConnector {

	private final String connectorName;
	/** url du serveur elasticSearch. */
	private final String[] serversNames;
	/** le noeud interne. */
	private TransportClient client;
	/** Config pour ES **/
	private final Settings settings;

	/**
	 * Constructor.
	 *
	 * @param serversNamesStr URL du serveur ElasticSearch avec le port de communication de cluster (9300 en général)
	 * @param envIndex Nom de l'index de l'environment
	 * @param envIndexIsPrefix Si Nom de l'index de l'environment est un prefix
	 * @param rowsPerQuery Liste des indexes
	 * @param clusterName : nom du cluster à rejoindre
	 * @param nodeNameOpt : nom du node
	 * @param securityEnabled active ou non la sécurité
	 * @param securityUser l'utilisateur ES
	 * @param securityKey la clé privé (si chiffrée doit être en AES)
	 * @param securityKeyPassPhrase passphrase si clé chiffrée
	 * @param securityCertificate le certificat
	 * @param configFile fichier de configuration des index
	 */
	@Inject
	public SecuredTransportSearchConnector(
			@ParamValue("name") final Optional<String> connectorNameOpt,
			@ParamValue("servers.names") final String serversNamesStr,
			@ParamValue("envIndex") final String envIndex,
			@ParamValue("envIndexIsPrefix") final Optional<Boolean> envIndexIsPrefix,
			@ParamValue("rowsPerQuery") final int rowsPerQuery, @ParamValue("cluster.name") final String clusterName,
			@ParamValue("config.file") final String configFile,
			@ParamValue("node.name") final Optional<String> nodeNameOpt,
			@ParamValue("security.enabled") final Optional<Boolean> securityEnabled,
			@ParamValue("security.user") final Optional<String> securityUser,
			@ParamValue("security.password") final Optional<String> securityPassword,
			@ParamValue("security.key") final Optional<String> securityKey,
			@ParamValue("security.key_passphrase") final Optional<String> securityKeyPassPhrase,
			@ParamValue("security.certificate") final Optional<String> securityCertificate) {
		Assertion.check()
				.isNotBlank(serversNamesStr,
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isFalse(serversNamesStr.contains(","),
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isNotBlank(clusterName,
						"Cluster's name must be defined")
				.isFalse("elasticsearch".equals(clusterName), "You must define a cluster name different from the default one")
				.when(securityEnabled.orElse(false), () -> Assertion.check()
						.isTrue(securityUser.isPresent()
								&& securityPassword.isPresent()
								&& securityKey.isPresent()
								&& securityCertificate.isPresent(),
								"When security is enabled, you must set securityUser, securityPassword, securityKey and securityCertificate"));

		// ---------------------------------------------------------------------
		connectorName = connectorNameOpt.orElse("main");
		serversNames = serversNamesStr.split(",");

		// prepare settings
		final Builder builder = Settings.builder()
				.put("cluster.name", clusterName)
				.put("node.name", nodeNameOpt.orElseGet(() -> "es-client-transport-secured-" + System.currentTimeMillis()));

		if (securityEnabled.orElse(false)) {
			builder.put("xpack.security.transport.ssl.enabled", true)
					.put("xpack.security.user", securityUser.get() + ':' + securityPassword.get())
					.put("xpack.security.transport.ssl.key", securityKey.get())
					.put("xpack.security.transport.ssl.certificate", securityCertificate.get());
			if (securityKeyPassPhrase.isPresent()) {
				builder.put("xpack.security.transport.ssl.key_passphrase", securityKeyPassPhrase.get());
			}
		}
		settings = builder.build();
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		client = new PreBuiltXPackTransportClient(settings);
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
}
