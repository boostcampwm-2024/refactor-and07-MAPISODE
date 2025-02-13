package com.boostcamp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boostcamp.mapisode.model.EpisodeLatLng
import com.boostcamp.mapisode.model.EpisodeModel
import java.util.Date

@Entity
data class EpisodeRoomEntity(
	@PrimaryKey
	val id: String,
	val category: String,
	val content: String,
	val createdBy: String,
	val group: String,
	val imageUrls: String,
	val address: String,
	val location: Pair<Double, Double>,
	val memoryDate: String,
	val tags: String,
	val title: String,
	val createdAt: Date,
	val createdByName: String,
	val imageUrlsUsedForOnlyUpdate: String,
)

fun EpisodeModel.toEpisodeRoomEntity(): EpisodeRoomEntity {
	return EpisodeRoomEntity(
		id = id,
		category = category,
		content = content,
		createdBy = createdBy,
		group = group,
		imageUrls = imageUrls.joinToString(","),
		address = address,
		location = location.latitude to location.longitude,
		memoryDate = memoryDate.toString(),
		tags = tags.joinToString(","),
		title = title,
		createdAt = createdAt,
		createdByName = createdByName,
		imageUrlsUsedForOnlyUpdate = imageUrlsUsedForOnlyUpdate.joinToString(","),
	)
}

fun EpisodeRoomEntity.toEpisodeModel(): EpisodeModel {
	return EpisodeModel(
		id = id,
		category = category,
		content = content,
		createdBy = createdBy,
		group = group,
		imageUrls = imageUrls.split(","),
		address = address,
		location = EpisodeLatLng(location.first, location.second),
		memoryDate = Date(memoryDate.toLong()),
		tags = tags.split(","),
		title = title,
		createdAt = createdAt,
		createdByName = createdByName,
		imageUrlsUsedForOnlyUpdate = imageUrlsUsedForOnlyUpdate.split(","),
	)
}
