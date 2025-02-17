package com.boostcamp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boostcamp.mapisode.model.GroupModel
import java.util.Date

@Entity
data class GroupRoomEntity(
	@PrimaryKey
	val id: String,
	val adminUser: String,
	val createdAt: Date,
	val description: String,
	val imageUrl: String,
	val name: String,
	val members: String,
)

fun GroupRoomEntity.toGroupModel() = GroupModel(
	id = id,
	adminUser = adminUser,
	createdAt = createdAt,
	description = description,
	imageUrl = imageUrl,
	name = name,
	members = members.split(","),
)
