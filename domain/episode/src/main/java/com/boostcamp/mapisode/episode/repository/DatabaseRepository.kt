package com.boostcamp.mapisode.episode.repository

import com.boostcamp.mapisode.model.EpisodeLatLng
import com.boostcamp.mapisode.model.EpisodeModel
import com.boostcamp.mapisode.model.GroupModel
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
	// 에피소드
	suspend fun insertEpisode(episode: EpisodeModel)
	fun getEpisodeByGroupId(groupId: String): Flow<List<EpisodeModel>>
	fun getEpisodeByGroupAndCategory(groupId: String, category: String): Flow<List<EpisodeModel>>
	fun getEpisodeByEpisodeId(episodeId: String): Flow<EpisodeModel>
	fun getMostRecentEpisodeByGroup(groupId: String): Flow<EpisodeModel>
	fun getEpisodesByGroupAndLocation(
		groupId: String,
		start: EpisodeLatLng,
		end: EpisodeLatLng,
		category: String?,
	): Flow<List<EpisodeModel>>

	// 그룹
	fun getGroupByGroupId(groupId: String): Flow<GroupModel>
	fun getAllGroups(): Flow<List<GroupModel>>
	suspend fun insertOrUpdateGroup(groupModel: GroupModel)
}
