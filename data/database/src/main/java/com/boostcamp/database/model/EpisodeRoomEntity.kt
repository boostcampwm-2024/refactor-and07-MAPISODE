package com.boostcamp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class EpisodeRoomEntity(
	@PrimaryKey
	val id: String,
	val category: String,
	val content: String,
	val createdBy: String,
	val group: String,
	val imageUrls: List<String>,
	val address: String,
	val location: Pair<Double, Double>,
	val memoryDate: String,
	val tags: List<String>,
	val title: String,
	val createdAt: Date,
)
