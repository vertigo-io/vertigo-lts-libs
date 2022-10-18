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
package io.vertigo.struts2.ui.controller.accueil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Date;

import javax.inject.Inject;

import io.vertigo.account.security.VSecurityManager;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.FSFile;
import io.vertigo.struts2.core.AbstractActionSupport.AcceptCtxQueryParam;
import io.vertigo.struts2.core.ContextForm;
import io.vertigo.struts2.core.ContextList;
import io.vertigo.struts2.core.ContextListModifiable;
import io.vertigo.struts2.core.ContextMdl;
import io.vertigo.struts2.core.ContextRef;
import io.vertigo.struts2.core.ContextVFile;
import io.vertigo.struts2.core.ContextVFiles;
import io.vertigo.struts2.core.GET;
import io.vertigo.struts2.data.domain.DtDefinitions.MovieDisplayFields;
import io.vertigo.struts2.data.domain.movies.Movie;
import io.vertigo.struts2.data.domain.movies.MovieDisplay;
import io.vertigo.struts2.data.domain.people.Casting;
import io.vertigo.struts2.data.domain.reference.Commune;
import io.vertigo.struts2.data.domain.reference.OuiNonChoice;
import io.vertigo.struts2.services.movies.MovieServices;
import io.vertigo.struts2.ui.TestUserSession;
import io.vertigo.struts2.ui.controller.AbstractTestActionSupport;
import io.vertigo.vega.webservice.validation.UiMessageStack.Level;

@AcceptCtxQueryParam
public class AccueilAction extends AbstractTestActionSupport {

	private static final long serialVersionUID = 1L;

	private final ContextForm<Movie> movie = new ContextForm<>("movie", this);
	private final ContextForm<Casting> casting = new ContextForm<>("casting", this);
	private final ContextList<Movie> movieList = new ContextList<>("movies", this);
	private final ContextListModifiable<Movie> movieListModifiables = new ContextListModifiable<>("moviesModifiable", this);
	private final ContextMdl<Movie> moviesListMdl = new ContextMdl<>("moviesMdl", this);
	private final ContextRef<String> communeId = new ContextRef<>("communeId", String.class, this);
	private final ContextMdl<Commune> communeListMdl = new ContextMdl<>("communesMdl", this);
	private final ContextList<OuiNonChoice> ouiNonList = new ContextList<>("ouiNon", this);

	private final ContextList<MovieDisplay> movieDisplayList = new ContextList<>("moviesDisplay", MovieDisplayFields.movId, this);

	private final ContextRef<String> currentDate = new ContextRef<>("currentDate", String.class, this);

	private final ContextVFile fileTestFileRef = new ContextVFile("fileTest", this);
	private final ContextVFiles filesTestFileRef = new ContextVFiles("filesTest", this);

	private final ContextRef<String> currentZoneId = new ContextRef<>("currentZoneId", String.class, this);
	private static final String[] timeZoneListStatic = { "Europe/Paris", "America/Cayenne", "Indian/Reunion" };
	private final ContextRef<String[]> timeZoneList = new ContextRef<>("timeZoneList", String[].class, this);
	private final ContextRef<String> zoneId = new ContextRef<>("zoneId", String.class, this);

	@Inject
	private MovieServices movieServices;

	@Inject
	private VSecurityManager securityManager;

	@Inject
	private LocaleManager localeManager;

	@Override
	protected void initContext() {
		movie.publish(new Movie());
		casting.publish(new Casting());
		movieList.publish(movieServices.getMovies(DtListState.defaultOf(Movie.class)));
		movieListModifiables.publish(movieServices.getMovies(DtListState.defaultOf(Movie.class)));
		moviesListMdl.publish(Movie.class, null);
		communeListMdl.publish(Commune.class, null);
		ouiNonList.publish(movieServices.getOuiNonChoice());
		movieDisplayList.publish(movieServices.getMoviesDisplay(DtListState.defaultOf(Movie.class)));

		toModeCreate();
		currentDate.set(new Date().toString());

		currentZoneId.set(localeManager.getCurrentZoneId().getId());
		zoneId.set(timeZoneListStatic[0]);
		timeZoneList.set(timeZoneListStatic);
	}

	public String save() {
		movie.readDto();
		currentDate.set(new Date().toString());
		return NONE;
	}

	public String saveCasting() {
		casting.readDto();
		return NONE;
	}

	public String saveCommune() {
		communeId.set(communeId.get());
		return NONE;
	}

	public String saveInstant() {
		currentDate.set(movie.readDto().getLastModified().toString());

		final TestUserSession userSession = securityManager.<TestUserSession> getCurrentUserSession().get();
		userSession.setZoneId(ZoneId.of(zoneId.get()));
		currentZoneId.set(localeManager.getCurrentZoneId().getId());
		return NONE;
	}

	public String addMovieList() {
		movieListModifiables.getUiListModifiable().add(new Movie());
		return NONE;
	}

	public String saveList() {
		movieListModifiables.readDtList();
		return NONE;
	}

	public String uploadFile() {
		if (!fileTestFileRef.exists()) {
			throw new VUserException("Aucun fichier uploadé.");
		}
		final VFile vFile = fileTestFileRef.get();
		getUiMessageStack().addGlobalMessage(Level.INFO, "Fichier recu : " + vFile.getFileName() + " (" + vFile.getMimeType() + ")");
		return NONE;
	}

	public String uploadFiles() {
		if (!filesTestFileRef.exists()) {
			throw new VUserException("Aucun fichiers uploadé.");
		}
		final VFile[] vFiles = filesTestFileRef.get();
		getUiMessageStack().addGlobalMessage(Level.INFO, vFiles.length + " fichiers recus");
		for (final VFile vFile : vFiles) {
			getUiMessageStack().addGlobalMessage(Level.INFO, "Fichier recu : " + vFile.getFileName() + " (" + vFile.getMimeType() + ")");
		}
		return NONE;
	}

	/**
	 * Exporte l'annuaire utilisateur.
	 * @return redirection struts
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@GET
	public String downloadFile() throws IOException, URISyntaxException {
		final URI fullPath = getClass().getResource("/data/insee.csv").toURI();
		final Path localFile = Paths.get(fullPath);
		final VFile vFile = new FSFile("insee.csv", "text/csv", localFile);
		return createVFileResponseBuilder().send(vFile);
	}

	public String toRead() {
		toModeReadOnly();
		return NONE;
	}

	public String toEdit() {
		toModeEdit();
		return NONE;
	}

	@Override
	public String getPageName() {
		return "Accueil";
	}
}
