/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2022, Vertigo.io, team@vertigo.io
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
package io.vertigo.account.impl.authorization.xml;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.account.authorization.ResourceNameFactory;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.util.BeanUtil;

/**
 * ResourceNameFactory standard des beans securisees.
 * @author npiedeloup
 */
public final class BeanResourceNameFactory implements ResourceNameFactory {
	private final String securityPattern;
	private final List<String> securityPatternTokenized = new ArrayList<>();

	/**
	 * Constructor.
	 * Prend en entrée le pattern de la chaine de resource à produire.
	 * Il peut être paramétré avec des propriétés de l'objet avec la syntaxe : ${maPropriete}
	 * @param securityPattern Pattern de la resource.
	 */
	public BeanResourceNameFactory(final String securityPattern) {
		Assertion.check().isNotNull(securityPattern);
		//-----
		this.securityPattern = securityPattern;
		int previousIndex = 0;
		int nextIndex = securityPattern.indexOf("${", previousIndex);
		while (nextIndex >= 0) {
			securityPatternTokenized.add(securityPattern.substring(previousIndex, nextIndex));
			final int endIndex = securityPattern.indexOf('}', nextIndex + "${".length());
			Assertion.check().isTrue(endIndex >= nextIndex, "accolade fermante non trouvee : {0} a  {1}", securityPattern, nextIndex);
			final String key = securityPattern.substring(nextIndex + "${".length(), endIndex);
			securityPatternTokenized.add("$" + key);
			previousIndex = endIndex + "}".length();
			nextIndex = securityPattern.indexOf("${", previousIndex);
		}
		if (previousIndex < securityPattern.length()) {
			securityPatternTokenized.add(securityPattern.substring(previousIndex, securityPattern.length()));
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toResourceName(final Object value) {
		final StringBuilder sb = new StringBuilder(securityPattern.length());
		for (final String token : securityPatternTokenized) {
			if (token.startsWith("$")) {
				final String key = token.substring("$".length());
				sb.append(String.valueOf(BeanUtil.getValue(value, key)));
			} else {
				sb.append(token);
			}
		}
		return sb.toString();
	}
}
