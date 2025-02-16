package com.boostcamp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boostcamp.mapisode.firebase.firestore.FirestoreConstants.FIELD_ADMIN_USER
import com.boostcamp.mapisode.firebase.firestore.FirestoreConstants.FIELD_MEMBERS
import com.boostcamp.mapisode.model.GroupModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import kotlin.collections.map

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

fun GroupRoomEntity.toGroupFirestoreModel(database: FirebaseFirestore) = GroupFirestoreModel(
	id = id,
	adminUser = database.collection(FIELD_ADMIN_USER).document(adminUser),
	createdAt = Timestamp(createdAt),
	description = description,
	imageUrl = imageUrl,
	name = name,
	members = members.split(",").map { database.collection(FIELD_MEMBERS).document(it) },
)
