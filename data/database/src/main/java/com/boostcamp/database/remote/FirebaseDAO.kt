package com.boostcamp.database.remote

import androidx.core.net.toUri
import com.boostcamp.database.model.EpisodeFirestoreModel
import com.boostcamp.database.model.EpisodeRoomEntity
import com.boostcamp.database.model.GroupFirestoreModel
import com.boostcamp.database.model.GroupRoomEntity
import com.boostcamp.database.model.toEpisodeFirestoreModel
import com.boostcamp.database.model.toEpisodeRoomEntity
import com.boostcamp.database.model.toGroupRoomEntity
import com.boostcamp.mapisode.firebase.firestore.FirestoreConstants
import com.boostcamp.mapisode.firebase.firestore.StorageConstants.PATH_IMAGES
import com.boostcamp.mapisode.model.EpisodeModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FirebaseDAO(private val firestore: FirebaseFirestore, private val storage: FirebaseStorage) {

	private val groupCollection = firestore.collection(FirestoreConstants.COLLECTION_GROUP)
	private val episodeCollection = firestore.collection(FirestoreConstants.COLLECTION_EPISODE)
	private val userCollection = firestore.collection(FirestoreConstants.COLLECTION_USER)
	private val inviteCodesCollection =
		firestore.collection(FirestoreConstants.COLLECTION_INVITE_CODES)

	// 특정 유저의 그룹 조회
	suspend fun getGroupsByUserId(userId: String): List<GroupRoomEntity> = try {
		val userSnapshot = userCollection.document(userId).get().await()

		@Suppress("UNCHECKED_CAST")
		val groupReferences =
			(userSnapshot[FirestoreConstants.FIELD_GROUPS] as List<DocumentReference>)

		groupReferences.mapNotNull { documentRef ->
			groupCollection.document(documentRef.id)
				.get()
				.await()
				.toObject(GroupFirestoreModel::class.java)?.toGroupRoomEntity(documentRef.id)
		}
	} catch (e: Exception) {
		throw e
	}

	// 초대 코드 발급
	suspend fun getGroupByInviteCodes(inviteCodes: String): GroupRoomEntity = try {
		val groupSnapshot = inviteCodesCollection.document(inviteCodes)
			.get().await()
		val group = groupSnapshot[FirestoreConstants.FIELD_GROUP] as DocumentReference

		groupCollection.document(group.id)
			.get()
			.await()
			.toObject(GroupFirestoreModel::class.java)?.toGroupRoomEntity(group.id)
			?: throw Exception("그룹을 찾을 수 없습니다.")
	} catch (e: Exception) {
		throw e
	}

	// 에피소드 이미지 업로드
	suspend fun uploadImagesToStorage(
		newEpisodeId: String,
		imageUris: List<String>,
	): List<String> {
		try {
			val imageStorageUrls = coroutineScope {
				imageUris.mapIndexed { index, imageUri ->
					async(Dispatchers.IO) {
						val imageRef =
							storage.reference.child("$PATH_IMAGES/$newEpisodeId/${index + 1}")
						imageRef.putFile(imageUri.toUri()).continueWithTask { task ->
							if (task.isSuccessful.not()) {
								task.exception?.let { e ->
									throw e
								}
							}
							imageRef.downloadUrl
						}.addOnCompleteListener { task ->
							if (task.isSuccessful) {
								val imageUrl = task.result
								Timber.d("image url: $imageUrl")
							} else {
								task.exception?.let { e ->
									throw e
								}
							}
						}.await()
					}
				}.awaitAll()
			}
			return imageStorageUrls.map { it.toString() }
		} catch (e: Exception) {
			throw e
		}
	}

	// 에피소드 생성
	@OptIn(ExperimentalUuidApi::class)
	suspend fun createEpisode(
		episodeModel: EpisodeModel,
		uploadedImageUrls: List<String>,
	): String {
		val newEpisodeId = Uuid.random().toString().replace("-", "")
		return try {
			episodeCollection
				.document(newEpisodeId)
				.set(episodeModel.toEpisodeFirestoreModel(firestore, uploadedImageUrls))
				.await()
			newEpisodeId
		} catch (e: Exception) {
			throw e
		}
	}

	// 특정 그룹의 에피소드 조회
	suspend fun getEpisodesByGroup(groupId: String): List<EpisodeRoomEntity> {
		val query = episodeCollection
			.whereEqualTo(
				FirestoreConstants.FIELD_GROUP,
				groupCollection.document(groupId),
			)
		val querySnapshot = query.get().await()
		if (querySnapshot.isEmpty) {
			return emptyList()
		}

		return try {
			querySnapshot.map { document ->
				val model = requireNotNull(document.toObject(EpisodeFirestoreModel::class.java))
				model.toEpisodeRoomEntity(document.id)
			}
		} catch (e: Exception) {
			throw e
		}
	}
}
