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
package io.vertigo.account.plugins.authorization.xml.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import javax.inject.Named;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.core.util.StringUtil;
import io.vertigo.core.util.XmlUtil;

/**
 * Plugin XML chargeant la registry à partir d'un fichier XML
 * La définition du fichier XML est décrite dans le fichier authorisation-config_1_0.dtd
 * Un exemple de fichier:
 *
 * <authorisation-config>
 *
 *	<!--  Ressources -->
 *	<resource id="all_products" filter="/products/.*" description="Liste des produits"/>
 *
 *	<!--  Permissions -->
 *	<permission id="read_all_products" operation="read" resource="all_products" description="Lire tous les produits"/>
 *	<permission id="write_all_products" operation="write" resource="all_products" description="Créer/Modifier un produit"/>
 *
 *	<!-- Roles -->
 *	<role name="reader" description="Lecteur de l'application">
 * 		<permission ref="read_all_products"/>
 *  	</role>
 *  	<role name="writer" description="Ecrivain de l'application">
 *  		<permission ref="read_all_products"/>
 *  		<permission ref="write_all_products"/>
 *  	</role>
 * </authorisation-config>
 * @author prahmoune
 */
final class XmlSecurityLoader {
	private final URL authURL;

	/**
	 * Constructeur
	 * @param resourceManager Resource manager
	 * @param url Url du fichier XML de configuration
	 */
	XmlSecurityLoader(final ResourceManager resourceManager, @Named("url") final String url) {
		Assertion.check().isNotNull(resourceManager);
		Assertion.check().isNotBlank(url);
		//-----
		authURL = resourceManager.resolve(url);
	}

	XmlSecurityDefinition load() {
		Assertion.check().isNotNull(authURL);
		//-----
		try {
			return doLoadXML(authURL);
		} catch (final ParserConfigurationException pce) {
			throw WrappedException.wrap(pce, StringUtil.format("Erreur de configuration du parseur (fichier {0}), lors de l'appel à newSAXParser()", authURL.getPath()));
		} catch (final SAXException se) {
			throw WrappedException.wrap(se, StringUtil.format("Erreur de parsing (fichier {0}), lors de l'appel à parse()", authURL.getPath()));
		} catch (final IOException ioe) {
			throw WrappedException.wrap(ioe, StringUtil.format("Erreur d'entrée/sortie (fichier {0}), lors de l'appel à parse()", authURL.getPath()));
		}
	}

	private static XmlSecurityDefinition doLoadXML(final URL configURL) throws SAXException, IOException, ParserConfigurationException {
		xsdValidate(configURL);
		//---

		final XmlSecurityHandler handler = new XmlSecurityHandler();
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		final SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(new BufferedInputStream(configURL.openStream()), handler);
		return new XmlSecurityDefinition(handler.getPermissions(), handler.getRoles());
	}

	private static void xsdValidate(final URL configURL) {
		//--- validation XSD
		final URL xsd = XmlSecurityLoader.class.getResource("vertigo-security_1_0.xsd");
		XmlUtil.validateXmlByXsd(configURL, xsd);
		//--- fin validation XSD
	}

}
