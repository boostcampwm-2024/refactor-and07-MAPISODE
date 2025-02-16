package com.boostcamp.database.model

import com.boostcamp.mapisode.firebase.firestore.FirestoreConstants.COLLECTION_USER
import com.boostcamp.mapisode.firebase.firestore.FirestoreConstants.FIELD_MEMBERS
import com.boostcamp.mapisode.model.GroupModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class GroupFirestoreModel(
	val id: String = "",
	val adminUser: DocumentReference? = null,
	val createdAt: Timestamp = Timestamp.now(),
	val description: String = "",
	val imageUrl: String = "",
	val name: String = "",
	val members: List<DocumentReference> = listOf(),
)

fun GroupFirestoreModel.toGroupRoomEntity(groupId: String) = GroupRoomEntity(
	id = groupId,
	adminUser = adminUser?.id ?: "",
	createdAt = createdAt.toDate(),
	description = description,
	imageUrl = imageUrl,
	name = name,
	members = members.map { it.id }.joinToString(","),
)

fun GroupFirestoreModel.toGroupModel() = GroupModel(
	id = id,
	adminUser = adminUser?.id ?: "",
	createdAt = createdAt.toDate(),
	description = description,
	imageUrl = imageUrl,
	name = name,
	members = members.map { it.id },
)

fun GroupModel.toGroupFirestoreModel(database: FirebaseFirestore) = GroupFirestoreModel(
	id = id,
	adminUser = database.collection(COLLECTION_USER).document(adminUser),
	createdAt = Timestamp(createdAt),
	description = description,
	imageUrl = imageUrl,
	name = name,
	members = members.map { database.collection(FIELD_MEMBERS).document(it) },
)
