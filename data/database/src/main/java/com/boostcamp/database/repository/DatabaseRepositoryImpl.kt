package com.boostcamp.database.repository

import com.boostcamp.database.EpisodeDAO
import com.boostcamp.database.model.toEpisodeModel
import com.boostcamp.database.model.toEpisodeRoomEntity
import com.boostcamp.mapisode.episode.DatabaseRepository
import com.boostcamp.mapisode.model.EpisodeModel
import kotlinx.coroutines.flow.flow

class DatabaseRepositoryImpl(private val roomDAO: EpisodeDAO) :
	DatabaseRepository {
	override suspend fun cacheEpisode(episode: EpisodeModel) {
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

	override fun getAllEpisodesByGroup(group: String) = flow {
		roomDAO.getAllEpisodesByGroup(group).collect {
			emit(it.toEpisodeModel())
		}
	}
}
