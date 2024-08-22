/*
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2024, Vertigo.io, team@vertigo.io
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Connector;
import io.vertigo.core.param.ParamValue;
import io.vertigo.core.resource.ResourceManager;

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
	private final RestHighLevelClient client;

	/**
	 * Constructor.
	 *
	 * @param resourceManager Resource manager
	 * @param connectorNameOpt Connector name (default `main`)
	 * @param serversNamesStr Server names list (ex : host1:3889,host2:3889) separator : ','
	 * @param basicUserOpt Username if basic autgent
	 * @param basicPasswordOpt password if basic authent
	 * @param apiKeyIdOpt api key id if apiKey authent
	 * @param apiKeySecretOpt api key secret if apiKey authent
	 * @param ssl if using ssl
	 * @param trustStoreUrlOpt Url to truststore
	 * @param trustStorePasswordOpt truststore's password
	 */
	@Inject
	public RestHighLevelElasticSearchConnector(
			final ResourceManager resourceManager,
			@ParamValue("name") final Optional<String> connectorNameOpt,
			@ParamValue("servers.names") final String serversNamesStr,
			@ParamValue("username") final Optional<String> basicUserOpt,
			@ParamValue("password") final Optional<String> basicPasswordOpt,
			@ParamValue("apiKeyId") final Optional<String> apiKeyIdOpt,
			@ParamValue("apiKeySecret") final Optional<String> apiKeySecretOpt,
			@ParamValue("ssl") final boolean ssl,
			@ParamValue("trustStoreUrl") final Optional<String> trustStoreUrlOpt,
			@ParamValue("trustStorePassword") final Optional<String> trustStorePasswordOpt) {
		Assertion.check()
				.isNotBlank(serversNamesStr,
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.isFalse(serversNamesStr.contains(";"),
						"Il faut définir les urls des serveurs ElasticSearch (ex : host1:3889,host2:3889). Séparateur : ','")
				.when(basicUserOpt.isPresent(), () -> Assertion.check()
						.isTrue(basicUserOpt.isPresent() && basicPasswordOpt.isPresent(),
								"When username is enabled, you must set username and password"))
				.when(apiKeyIdOpt.isPresent(), () -> Assertion.check()
						.isTrue(apiKeyIdOpt.isPresent()
								&& apiKeySecretOpt.isPresent(),
								"When apiKey is enabled, you must set apiKeyId and apiKeySecret"))
				.when(apiKeyIdOpt.isPresent(), () -> Assertion.check()
						.isTrue(basicUserOpt.isEmpty() && basicPasswordOpt.isEmpty(),
								"When apiKey is enabled, you can't use basic authentication with user:password"))
				.when(ssl, () -> Assertion.check()
						.isTrue(trustStoreUrlOpt.isPresent()
								&& trustStorePasswordOpt.isPresent(),
								"When SSL is enabled, you must set apiKey, trustStoreUrl and trustStorePassword"));
		// ---------------------------------------------------------------------
		connectorName = connectorNameOpt.orElse("main");
		serversNames = serversNamesStr.split(",");

		final RestClientBuilder restClientBuilder = buildRestClientBuilder(resourceManager, basicUserOpt, basicPasswordOpt,
				apiKeyIdOpt, apiKeySecretOpt, ssl, trustStoreUrlOpt, trustStorePasswordOpt);
		client = new RestHighLevelClient(restClientBuilder);
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		try {
			client.ping(RequestOptions.DEFAULT);
		} catch (final Exception e) {
			throw WrappedException.wrap(e);
		}
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

	private static SSLContext createTrustStoreSslContext(final URL trustStoreUrl, final String trustStorePassword) throws Exception {
		final var trustStore = KeyStore.getInstance("pkcs12");
		try (var inputStream = trustStoreUrl.openStream()) {
			trustStore.load(inputStream, trustStorePassword.toCharArray());
		}

		final var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(trustStore);
		final var trustManagers = trustManagerFactory.getTrustManagers();

		final var sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(null, trustManagers, new SecureRandom());
		return sslContext;
	}

	protected RestClientBuilder buildRestClientBuilder(final ResourceManager resourceManager,
			final Optional<String> basicUserOpt, final Optional<String> basicPasswordOpt,
			final Optional<String> apiKeyIdOpt, final Optional<String> apiKeySecretOpt,
			final boolean ssl, final Optional<String> trustStoreUrlOpt, final Optional<String> trustStorePasswordOpt) {
		final List<HttpHost> httpHostList = new ArrayList<>();
		for (final String serverName : serversNames) {
			final String[] serverNameSplit = serverName.split(":");
			Assertion.check().isTrue(serverNameSplit.length == 2,
					"La déclaration du serveur doit être au format host:port ({0}).", serverName);
			final int port = Integer.parseInt(serverNameSplit[1]);
			httpHostList.add(new HttpHost(serverNameSplit[0], port, ssl ? "https" : "http"));
		}
		final HttpHost[] httpHosts = httpHostList.toArray(new HttpHost[httpHostList.size()]);
		final RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);

		if (apiKeyIdOpt.isPresent()) {
			final String apiKeyAuth = Base64.getEncoder().encodeToString((apiKeyIdOpt.get() + ":" + apiKeySecretOpt.get()).getBytes(StandardCharsets.UTF_8));
			final Header[] defaultHeaders = new Header[] { new BasicHeader("Authorization", "ApiKey " + apiKeyAuth) };
			restClientBuilder.setDefaultHeaders(defaultHeaders);
		}

		final CredentialsProvider credentialsProvider;
		if (basicUserOpt.isPresent()) {
			credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(basicUserOpt.get(), basicPasswordOpt.get()));
		} else {
			credentialsProvider = null;
		}

		final SSLContext sslContext;
		if (trustStoreUrlOpt.isPresent()) {
			try {
				sslContext = createTrustStoreSslContext(resourceManager.resolve(trustStoreUrlOpt.get()), trustStorePasswordOpt.get());

			} catch (final Exception e) {
				throw WrappedException.wrap(e);
			}
		} else {
			sslContext = null;
		}

		if (credentialsProvider != null || sslContext != null) {
			final HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigCallback() {
				@Override
				public HttpAsyncClientBuilder customizeHttpClient(final HttpAsyncClientBuilder httpClientBuilder) {
					if (credentialsProvider != null) {
						httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
					if (sslContext != null) {
						httpClientBuilder.setSSLContext(sslContext);
					}
					return httpClientBuilder;
				}
			};
			restClientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);
		}

		return restClientBuilder;

	}
}
