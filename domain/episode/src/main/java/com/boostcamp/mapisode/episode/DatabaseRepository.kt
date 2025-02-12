package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.model.EpisodeModel
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
	suspend fun cacheEpisode(episode: EpisodeModel)
	fun getAllEpisodesByGroup(group: String): Flow<EpisodeModel>
}
