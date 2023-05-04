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
package io.vertigo.struts2.services.movies;

import javax.inject.Inject;

import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.struts2.data.dao.movies.MovieDAO;
import io.vertigo.struts2.data.domain.movies.Movie;
import io.vertigo.struts2.data.domain.movies.MovieDisplay;
import io.vertigo.struts2.data.domain.reference.OuiNonChoice;

@Transactional
public class MovieServicesImpl implements MovieServices, Activeable {

	private DtList<OuiNonChoice> OUI_NON_LIST;

	@Inject
	private MovieDAO movieDAO;

	@Override
	public Movie get(final Long movId) {
		return movieDAO.get(movId);
	}

	@Override
	public void save(final Movie movie) {
		movieDAO.save(movie);
	}

	@Override
	public DtList<OuiNonChoice> getOuiNonChoice() {
		return OUI_NON_LIST;
	}

	@Override
	@Transactional
	public DtList<Movie> getMovies(final DtListState dtListState) {
		return movieDAO.findAll(Criterions.alwaysTrue(), dtListState);
	}

	@Override
	@Transactional
	public DtList<MovieDisplay> getMoviesDisplay(final DtListState dtListState) {
		return movieDAO.findAll(Criterions.alwaysTrue(), dtListState)
				.stream()
				.map(movie -> {
					final MovieDisplay movieDisplay = new MovieDisplay();
					movieDisplay.setMovId(movie.getMovId());
					movieDisplay.setTitle(movie.getTitle());
					return movieDisplay;
				})
				.collect(VCollectors.toDtList(MovieDisplay.class));
	}

	@Override
	public void start() {
		OUI_NON_LIST = new DtList<>(OuiNonChoice.class);
		final OuiNonChoice oui = new OuiNonChoice();
		oui.setKey(true);
		oui.setLibelle("oui");
		OUI_NON_LIST.add(oui);

		final OuiNonChoice non = new OuiNonChoice();
		non.setKey(true);
		non.setLibelle("non");
		OUI_NON_LIST.add(non);
	}

	@Override
	public void stop() {
		//rien
	}

}
