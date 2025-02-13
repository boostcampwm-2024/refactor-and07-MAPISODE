package com.boostcamp.database

import androidx.room.Dao
import androidx.room.Query
import com.boostcamp.database.model.EpisodeRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface EpisodeDAO {
	@Query("SELECT * FROM EpisodeRoomEntity WHERE `group` = :group")
	fun getAllEpisodesByGroup(group: String): Flow<EpisodeRoomEntity>

	@Query(
		"""
        INSERT OR REPLACE INTO EpisodeRoomEntity
        (id, category, content, createdBy, `group`, imageUrls, address, location, memoryDate, tags, title, createdAt, createdByName, imageUrlsUsedForOnlyUpdate)
        VALUES
        (:id, :category, :content, :createdBy, :group, :imageUrls, :address, :location, :memoryDate, :tags, :title, :createdAt, :createdByName, :imageUrlsUsedForOnlyUpdate)
    """,
	)
	suspend fun insertEpisode(
		id: String,
		category: String,
		content: String,
		createdBy: String,
		group: String,
		imageUrls: List<String>,
		address: String,
		location: Pair<Double, Double>,
		memoryDate: String,
		tags: List<String>,
		title: String,
		createdAt: Date,
		createdByName: String,
		imageUrlsUsedForOnlyUpdate: List<String>,
	)
}
