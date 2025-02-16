package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.model.EpisodeModel
import com.boostcamp.mapisode.model.GroupModel
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
	suspend fun createEpisode(episode: EpisodeModel)
	suspend fun getAllEpisodes(group: String, userId: String): Flow<List<EpisodeModel>>
	suspend fun createGroup()
	suspend fun getAllGroups(userId: String): Flow<List<GroupModel>>
	fun getGroupByGroupId(groupId: String): Flow<GroupModel>
}
