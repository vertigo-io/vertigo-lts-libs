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
package io.vertigo.struts2.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.datastore.filestore.model.VFile;

/**
 * Builder d'envoi de Fichier.
 *
 * @author npiedeloup
 */
public final class VFileResponseBuilder {
	private static final String NOT_ALLOWED_IN_FILENAME = "\\/:*?\"<>|;";

	private final HttpServletResponse httpResponse;

	/**
	 * Constructeur.
	 * @param httpResponse ServletResponse
	 */
	public VFileResponseBuilder(final HttpServletResponse httpResponse) {
		Assertion.check().isNotNull(httpResponse);
		//-----
		this.httpResponse = httpResponse;
	}

	/**
	 * Envoi les données au client sous forme d'attachment.
	 * @param vFile Fichier a envoyer
	 */
	public String send(final VFile vFile) {
		send(vFile, true);
		return null;
	}

	/**
	 * Envoi les données au client sous forme de stream.
	 * @param vFile Fichier a envoyer
	 */
	public void sendAsStream(final VFile vFile) {
		send(vFile, false);
	}

	private void send(final VFile vFile, final boolean attachment) {
		try {
			doSend(vFile, attachment);
		} catch (final IOException e) {
			handleException(e);
		}
	}

	private void doSend(final VFile vFile, final boolean attachment) throws IOException {
		final Long length = vFile.getLength();
		Assertion.check().isTrue(length.longValue() < Integer.MAX_VALUE,
				"Le fichier est trop gros pour être envoyé. Il fait " + length.longValue() / 1024 + " Ko, mais le maximum acceptable est de " + (Integer.MAX_VALUE / 1024) + " Ko.");
		httpResponse.setContentLength(length.intValue());
		httpResponse.addHeader("Content-Disposition", encodeFileNameToContentDisposition(vFile.getFileName(), attachment));
		httpResponse.setDateHeader("Last-Modified", vFile.getLastModified().toEpochMilli());
		httpResponse.setContentType(vFile.getMimeType());

		try (final InputStream input = vFile.createInputStream()) {
			try (final BufferedInputStream bInput = new BufferedInputStream(input)) {
				try (final OutputStream output = httpResponse.getOutputStream()) {
					copy(bInput, output);
				}
			}
		}
	}

	/**
	 * Gestion des exceptions d'export.
	 * @param exception L'exception à gérer
	 */
	private static void handleException(final Exception exception) {
		throw WrappedException.wrap(exception, "Impossible d'envoyer le fichier.<!-- {0} -->", exception.getMessage());
	}

	/**
	 * Encode fileName according to RFC 5987.
	 * @param fileName String
	 * @param isAttachment boolean is Content an attachment
	 * @return String
	 */
	private static String encodeFileNameToContentDisposition(final String fileName, final boolean isAttachment) {
		if (fileName == null) {
			return "";
		}
		// on remplace par des espaces les caractères interdits dans les noms de fichiers" : \ / : * ? " < > | ;
		final int notAllowedLength = NOT_ALLOWED_IN_FILENAME.length();
		String cleanFileName = fileName; //only accepted char
		for (int i = 0; i < notAllowedLength; i++) {
			cleanFileName = cleanFileName.replace(NOT_ALLOWED_IN_FILENAME.charAt(i), '_');
		}

		final int length = cleanFileName.length();
		final StringBuilder sb = new StringBuilder(length + length / 4);
		if (isAttachment) {
			sb.append("attachment;");
		}
		final String cleanestFileName = cleanFileName.replaceAll(" ", "%20"); //cleanest for default fileName
		sb.append("filename=").append(cleanestFileName);
		byte[] utf8FileName;
		try {
			utf8FileName = cleanFileName.getBytes("utf8"); //Utf8 fileName
			sb.append(";filename*=UTF-8''");
			for (final byte c : utf8FileName) {
				if (c == '.' || c == '-' || c == '_' || isSimpleLetterOrDigit(c)) {
					sb.append((char) c);
				} else {
					sb.append('%');
					sb.append(Integer.toHexString(c & 0xff)); // we want byte as a char on one byte
				}
			}
		} catch (final UnsupportedEncodingException e) {
			throw new AssertionError(e); // can't ever happen
		}
		return sb.toString();
	}

	/**
	 * Copie le contenu d'un flux d'entrée vers un flux de sortie.
	 * @param in flux d'entrée
	 * @param out flux de sortie
	 * @throws IOException Erreur d'entrée/sortie
	 */
	private static void copy(final InputStream in, final OutputStream out) throws IOException {
		final int bufferSize = 10 * 1024;
		final byte[] bytes = new byte[bufferSize];
		int read = in.read(bytes);
		while (read != -1) {
			out.write(bytes, 0, read);
			read = in.read(bytes);
		}
	}

	private static boolean isSimpleLetterOrDigit(final byte c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
	}
}
