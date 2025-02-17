package com.boostcamp.mapisode.episode.di

import com.boostcamp.mapisode.episode.EpisodeRepositoryImpl
import com.boostcamp.mapisode.episode.repository.EpisodeRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
	@Provides
	@Singleton
	fun bindEpisodeRepository(
		database: FirebaseFirestore,
		storage: FirebaseStorage,
	): EpisodeRepository {
		return EpisodeRepositoryImpl(database, storage)
	}
}
