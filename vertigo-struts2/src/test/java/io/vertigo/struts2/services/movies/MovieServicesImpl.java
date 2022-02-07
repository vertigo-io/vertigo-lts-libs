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
package io.vertigo.struts2.services.movies;

import javax.inject.Inject;

import io.vertigo.commons.transaction.Transactional;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.struts2.data.dao.movies.MovieDAO;
import io.vertigo.struts2.data.domain.movies.Movie;
import io.vertigo.struts2.data.domain.movies.MovieDisplay;

@Transactional
public class MovieServicesImpl implements MovieServices {

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

}
