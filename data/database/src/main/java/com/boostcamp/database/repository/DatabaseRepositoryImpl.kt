package com.boostcamp.database.repository

import com.boostcamp.database.local.RoomDAO
import com.boostcamp.database.model.toEpisodeModel
import com.boostcamp.database.model.toEpisodeRoomEntity
import com.boostcamp.database.model.toGroupModel
import com.boostcamp.mapisode.episode.repository.DatabaseRepository
import com.boostcamp.mapisode.model.EpisodeLatLng
import com.boostcamp.mapisode.model.EpisodeModel
import com.boostcamp.mapisode.model.GroupModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseRepositoryImpl(private val roomDAO: RoomDAO) : DatabaseRepository {
	override suspend fun insertEpisode(episode: EpisodeModel) {
		val en = episode.toEpisodeRoomEntity()
		roomDAO.insertEpisode(
			id = en.id,
			group = en.group,
			category = en.category,
			content = en.content,
			createdBy = en.createdBy,
			imageUrls = en.imageUrls.split(","),
			address = en.address,
			location = en.location,
			memoryDate = en.memoryDate,
			tags = en.tags.split(","),
			title = en.title,
			createdAt = en.createdAt,
			createdByName = en.createdByName,
			imageUrlsUsedForOnlyUpdate = en.imageUrlsUsedForOnlyUpdate.split(","),
		)
	}

	override fun getEpisodeByGroupId(groupId: String): Flow<List<EpisodeModel>> =
		roomDAO.getAllEpisodesByGroup(groupId)
			.map { list -> list.map { it.toEpisodeModel() } }

	override fun getEpisodeByGroupAndCategory(groupId: String, category: String): Flow<List<EpisodeModel>> =
		roomDAO.getEpisodeByGroupAndCategory(groupId, category)
			.map { list -> list.map { it.toEpisodeModel() } }

	override fun getEpisodeByEpisodeId(episodeId: String): Flow<EpisodeModel> =
		roomDAO.getEpisodeByEpisodeId(episodeId)
			.map { it.toEpisodeModel() }

	override fun getMostRecentEpisodeByGroup(groupId: String): Flow<EpisodeModel> =
		roomDAO.getMostRecentEpisodeByGroup(groupId)
			.map { it.toEpisodeModel() }

	override fun getEpisodesByGroupAndLocation(
		groupId: String,
		start: EpisodeLatLng,
		end: EpisodeLatLng,
		category: String?,
	): Flow<List<EpisodeModel>> {
		return roomDAO.getEpisodesByGroupAndLocation(
			groupId = groupId,
			start = start.longitude to start.latitude,
			end = end.longitude to end.latitude,
			category = category,
		).map { list -> list.map { it.toEpisodeModel() } }
	}

	override fun getGroupByGroupId(groupId: String): Flow<GroupModel> =
		roomDAO.getGroupByGroupId(groupId)
			.map { it.toGroupModel() }

	override fun getAllGroups(): Flow<List<GroupModel>> = roomDAO.getAllGroups()
		.map { list -> list.map { it.toGroupModel() } }

	override suspend fun insertOrUpdateGroup(groupModel: GroupModel) {
		roomDAO.insertGroup(
			id = groupModel.id,
			adminUser = groupModel.adminUser,
			createdAt = groupModel.createdAt,
			description = groupModel.description,
			imageUrl = groupModel.imageUrl,
			name = groupModel.name,
			members = groupModel.members.joinToString(","),
		)
	}
}
