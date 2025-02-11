package com.boostcamp.database

import androidx.room.Dao
import androidx.room.Query
import com.boostcamp.mapisode.model.EpisodeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDAO {
	@Query("SELECT * FROM EpisodeRoomEntity")
	fun getAllEpisodes(): Flow<EpisodeModel>

	@Query("INSERT OR REPLACE INTO EpisodeRoomEntity (id, category, content, createdBy, `group`, imageUrls, address, location, memoryDate, tags, title, createdAt) VALUES (:id, :category, :content, :createdBy, :group, :imageUrls, :address, :location, :memoryDate, :tags, :title, :createdAt)")
	fun insertEpisode(
		episode: EpisodeModel,
		id: String = episode.id,
		category: String = episode.category,
		content: String = episode.content,
		createdBy: String = episode.createdBy,
		group: String = episode.group,
		imageUrls: List<String> = episode.imageUrls,
		address: String = episode.address,
		location: Pair<Double, Double> = episode.location.latitude to episode.location.longitude,
		memoryDate: String = episode.memoryDate.toString(),
		tags: List<String> = episode.tags,
		title: String = episode.title,
		createdAt: String = episode.createdAt.toString(),
	)
}
