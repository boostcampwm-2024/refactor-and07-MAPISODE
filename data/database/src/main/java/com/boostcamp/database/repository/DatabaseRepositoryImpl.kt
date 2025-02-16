package com.boostcamp.database.repository

import com.boostcamp.database.local.RoomDAO
import com.boostcamp.database.model.toEpisodeModel
import com.boostcamp.database.model.toEpisodeRoomEntity
import com.boostcamp.database.model.toGroupModel
import com.boostcamp.database.remote.FirebaseDAO
import com.boostcamp.mapisode.episode.DatabaseRepository
import com.boostcamp.mapisode.model.EpisodeModel
import com.boostcamp.mapisode.model.GroupModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DatabaseRepositoryImpl(private val roomDAO: RoomDAO, private val firebaseDAO: FirebaseDAO) :
	DatabaseRepository {
	override suspend fun createEpisode(episode: EpisodeModel) {
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
		val storageUrls = firebaseDAO.uploadImagesToStorage(episode.id, episode.imageUrls)
		firebaseDAO.createEpisode(episode, storageUrls)
	}

	override suspend fun getAllEpisodes(groupId: String, userId: String): Flow<List<EpisodeModel>> {
		return try {
			val groups = firebaseDAO.getGroupsByUserId(userId)
			groups.forEach {
				roomDAO.insertGroup(
					id = it.id,
					adminUser = it.adminUser,
					createdAt = it.createdAt,
					description = it.description,
					imageUrl = it.imageUrl,
					name = it.name,
					members = it.members,
				)
			}

			flow {
				roomDAO.getAllEpisodesByGroup(groupId).collect {
					emit(it.map { entity -> entity.toEpisodeModel() })
				}
			}
		} catch (e: Exception) {
			flow { throw e }
		}
	}

	override suspend fun createGroup() {
	}

	override suspend fun getAllGroups(userId: String): Flow<List<GroupModel>> {
		return try {
			val groups = firebaseDAO.getGroupsByUserId(userId)
			groups.forEach {
				roomDAO.insertGroup(
					id = it.id,
					adminUser = it.adminUser,
					createdAt = it.createdAt,
					description = it.description,
					imageUrl = it.imageUrl,
					name = it.name,
					members = it.members,
				)
			}

			flow {
				roomDAO.getAllGroups().collect {
					emit(it.map { entity -> entity.toGroupModel() })
				}
			}
		} catch (e: Exception) {
			flow { throw e }
		}
	}

	override fun getGroupByGroupId(groupId: String): Flow<GroupModel> {
		return try {
			flow {
				roomDAO.getGroupByGroupId(groupId).collect {
					emit(it.toGroupModel())
				}
			}
		} catch (e: Exception) {
			flow { throw e }
		}
	}
}
