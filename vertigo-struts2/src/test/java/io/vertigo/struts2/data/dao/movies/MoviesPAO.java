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
package io.vertigo.struts2.data.dao.movies;

import javax.inject.Inject;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Generated;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datastore.impl.dao.StoreServices;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class MoviesPAO implements StoreServices {
	private final TaskManager taskManager;

	/**
	 * Constructeur.
	 * @param taskManager Manager des Task
	 */
	@Inject
	public MoviesPAO(final TaskManager taskManager) {
		Assertion.check().isNotNull(taskManager);
		//-----
		this.taskManager = taskManager;
	}

	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Node.getNode().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return Task.builder(taskDefinition);
	}

	/**
	 * Execute la tache StTkLoadMovieIndex.
	 * @param movieIds List de Long
	 * @return DtList de MovieIndex dtcIndex
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkLoadMovieIndex",
			request = "select MOV_ID," +
					"						 TITLE," +
					"						 TITLE as TITLE_SORT_ONLY," +
					"						 YEAR as PRODUCTION_YEAR," +
					"						 'Film' as MOVIE_TYPE," +
					"						 POSTER," +
					"						 TITLE as ORIGINAL_TITLE," +
					"						 RUNTIME," +
					"						 DESCRIPTION as SHORT_SYNOPSIS," +
					"						 DESCRIPTION as SYNOPSIS," +
					"						 RATED as USER_RATING" +
					"				from MOVIE mov" +
					"				where MOV_ID in (#movieIds.rownum#);",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtMovieIndex")
	public io.vertigo.datamodel.structure.model.DtList<io.vertigo.struts2.data.domain.movies.MovieIndex> loadMovieIndex(@io.vertigo.datamodel.task.proxy.TaskInput(name = "movieIds", smartType = "STyId") final java.util.List<Long> movieIds) {
		final Task task = createTaskBuilder("TkLoadMovieIndex")
				.addValue("movieIds", movieIds)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}
}
