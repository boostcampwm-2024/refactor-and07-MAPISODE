package com.boostcamp.database.local

import androidx.room.Dao
import androidx.room.Query
import com.boostcamp.database.model.EpisodeRoomEntity
import com.boostcamp.database.model.GroupRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RoomDAO {
	@Query("SELECT * FROM EpisodeRoomEntity WHERE `group` = :group")
	fun getAllEpisodesByGroup(group: String): Flow<List<EpisodeRoomEntity>>

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
		memoryDate: Date,
		tags: List<String>,
		title: String,
		createdAt: Date,
		createdByName: String,
		imageUrlsUsedForOnlyUpdate: List<String>,
	)

	@Query("SELECT * FROM GroupRoomEntity")
	fun getAllGroups(): Flow<List<GroupRoomEntity>>

	@Query("SELECT * FROM GroupRoomEntity WHERE id = :groupId")
	fun getGroupByGroupId(groupId: String): Flow<GroupRoomEntity>

	@Query(
		"""
		INSERT OR REPLACE INTO GroupRoomEntity
		(id, adminUser, createdAt, description, imageUrl, name, members)
		VALUES
		(:id, :adminUser, :createdAt, :description, :imageUrl, :name, :members)
	""",
	)
	suspend fun insertGroup(
		id: String,
		adminUser: String,
		createdAt: Date,
		description: String,
		imageUrl: String,
		name: String,
		members: String,
	)
}
