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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;

import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.mapper.MapperExtrasPlugin;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.reindex.ReindexPlugin;
import org.elasticsearch.transport.Netty4Plugin;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamValue;
import io.vertigo.core.resource.ResourceManager;

//Vérifier
/**
 * Gestion de la connexion au serveur ElasticSearch en mode embarqué.
 *
 * @author pchretien, npiedeloup
 */
public final class EmbeddedElasticSearchServer implements Component, Activeable {
	private static final int DEFAULT_TRANSPORT_PORT = 9300;

	private static final int DEFAULT_HTTP_PORT = 9200;

	public static final String DEFAULT_VERTIGO_ES_CLUSTER_NAME = "vertigo-elasticsearch-embedded";

	/** url du serveur elasticSearch.  */
	private final URL elasticSearchHomeURL;
	private Node node;

	private final String clusterName;
	private final Integer httpPort;
	private final Integer transportPort;

	/**
	 * Constructor.
	 * @param elasticSearchHome URL du serveur SOLR
	 * @param envIndex Nom de l'index de l'environment
	 * @param envIndexIsPrefix Si Nom de l'index de l'environment est un prefix
	 * @param rowsPerQuery Nombre d'élément retourné par query
	 * @param resourceManager Manager d'accès aux ressources
	 * @param configFile Fichier de configuration des indexs
	 */
	@Inject
	public EmbeddedElasticSearchServer(
			@ParamValue("home") final String elasticSearchHome,
			@ParamValue("cluster.name") final Optional<String> clusterNameOpt,
			@ParamValue("http.port") final Optional<Integer> httpPortOpt,
			@ParamValue("transport.tcp.port") final Optional<Integer> transportPortOpt,
			final ResourceManager resourceManager) {
		Assertion.check().isNotBlank(elasticSearchHome);
		//-----
		elasticSearchHomeURL = resourceManager.resolve(elasticSearchHome);
		clusterName = clusterNameOpt.orElse(DEFAULT_VERTIGO_ES_CLUSTER_NAME);
		httpPort = httpPortOpt.orElse(DEFAULT_HTTP_PORT);
		transportPort = transportPortOpt.orElse(DEFAULT_TRANSPORT_PORT);
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		node = createNode(elasticSearchHomeURL);
		try {
			node.start();
		} catch (final NodeValidationException e) {
			throw WrappedException.wrap(e, "Error at ElasticSearch node start");
		}
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

	private Node createNode(final URL esHomeURL) {
		Assertion.check().isNotNull(esHomeURL);
		//-----
		final File home;
		try {
			home = new File(URLDecoder.decode(esHomeURL.getFile(), StandardCharsets.UTF_8.name()));
		} catch (final UnsupportedEncodingException e) {
			throw WrappedException.wrap(e, "Error de parametrage du ElasticSearchHome {0}", esHomeURL);
		}
		Assertion.check()
				.isTrue(home.exists() && home.isDirectory(), "Le ElasticSearchHome : {0} n''existe pas, ou n''est pas un répertoire.", home.getAbsolutePath())
				.isTrue(home.canWrite(), "L''application n''a pas les droits d''écriture sur le ElasticSearchHome : {0}", home.getAbsolutePath());
		return new MyNode(buildNodeSettings(home.getAbsolutePath()), Arrays.asList(Netty4Plugin.class, ReindexPlugin.class, CommonAnalysisPlugin.class, MapperExtrasPlugin.class));
	}

	private static class MyNode extends Node {
		//Need to extends elastic Node, to access this advanced constructor and add plugins
		public MyNode(final Settings preparedSettings, final Collection<Class<? extends Plugin>> classpathPlugins) {
			super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, Collections.emptyMap(), null, null), classpathPlugins, true);
		}
	}

	private Settings buildNodeSettings(final String homePath) {
		//Build settings
		return Settings.builder()
				.put("node.name", "es-embedded-node-" + System.currentTimeMillis())
				.put("transport.type", "netty4")
				.put("http.type", "netty4")
				.put("http.port", httpPort)
				.put("transport.tcp.port", transportPort)
				.put("cluster.name", clusterName)
				.put("cluster.routing.allocation.disk.watermark.low", "1000mb")
				.put("cluster.routing.allocation.disk.watermark.high", "500mb")
				.put("cluster.routing.allocation.disk.watermark.flood_stage", "250mb")
				.put("path.home", homePath)
				.build();
	}
}
