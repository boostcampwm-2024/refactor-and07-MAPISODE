package com.boostcamp.database.di

import com.boostcamp.database.local.RoomDAO
import com.boostcamp.database.remote.FirebaseDAO
import com.boostcamp.database.repository.DatabaseRepositoryImpl
import com.boostcamp.mapisode.episode.DatabaseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBRepositoryModule {

	@Provides
	@Singleton
	fun provideFirebaseDAO(
		firebaseFirestore: FirebaseFirestore,
		firebaseStorage: FirebaseStorage,
	): FirebaseDAO {
		return FirebaseDAO(firebaseFirestore, firebaseStorage)
	}

	@Provides
	@Singleton
	fun provideDatabaseRepository(
		roomDao: RoomDAO,
		firebaseDAO: FirebaseDAO,
	): DatabaseRepository {
		return DatabaseRepositoryImpl(roomDao, firebaseDAO)
	}
}
