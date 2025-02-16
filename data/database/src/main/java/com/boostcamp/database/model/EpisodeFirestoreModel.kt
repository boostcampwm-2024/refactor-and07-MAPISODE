package com.boostcamp.database.model

import com.boostcamp.mapisode.firebase.firestore.FirestoreConstants.COLLECTION_GROUP
import com.boostcamp.mapisode.firebase.firestore.FirestoreConstants.COLLECTION_USER
import com.boostcamp.mapisode.model.EpisodeLatLng
import com.boostcamp.mapisode.model.EpisodeModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

data class EpisodeFirestoreModel(
	val category: String = "",
	val content: String = "",
	val createdBy: DocumentReference? = null,
	val group: DocumentReference? = null,
	val imageUrls: List<String> = emptyList(),
	val address: String = "",
	val location: GeoPoint? = null,
	val memoryDate: Timestamp = Timestamp.now(),
	val tags: List<String> = emptyList(),
	val title: String = "",
	val createdAt: Timestamp = Timestamp.now(),
)

fun EpisodeFirestoreModel.toEpisodeRoomEntity(episodeId: String) = EpisodeRoomEntity(
	id = episodeId,
	group = group?.id ?: "",
	category = category,
	content = content,
	createdBy = createdBy?.id ?: "",
	imageUrls = imageUrls.joinToString(","),
	address = address,
	location = location?.let { it.latitude to it.longitude } ?: (0.0 to 0.0),
	memoryDate = memoryDate.toDate(),
	tags = tags.joinToString(","),
	title = title,
	createdAt = createdAt.toDate(),
	createdByName = createdBy?.id ?: "",
	imageUrlsUsedForOnlyUpdate = imageUrls.joinToString(","),
)

fun EpisodeFirestoreModel.toEpisodeModel(episodeId: String) = EpisodeModel(
	id = episodeId,
	group = group?.id ?: "",
	category = category,
	content = content,
	createdBy = createdBy?.id ?: "",
	imageUrls = imageUrls,
	address = address,
	location = EpisodeLatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
	memoryDate = memoryDate.toDate(),
	tags = tags,
	title = title,
	createdAt = createdAt.toDate(),
	createdByName = createdBy?.id ?: "",
	imageUrlsUsedForOnlyUpdate = imageUrls,
)

fun EpisodeModel.toEpisodeFirestoreModel(database: FirebaseFirestore, imageUrls: List<String>) = EpisodeFirestoreModel(
	category = category,
	content = content,
	createdBy = database.collection(COLLECTION_USER).document(createdBy),
	group = database.collection(COLLECTION_GROUP).document(group),
	imageUrls = imageUrls,
	address = address,
	location = GeoPoint(location.latitude, location.longitude),
	memoryDate = Timestamp(memoryDate),
	tags = tags,
	title = title,
	createdAt = Timestamp(createdAt),
)
