package com.boostcamp.database.di

import com.boostcamp.database.EpisodeDAO
import com.boostcamp.database.repository.DatabaseRepositoryImpl
import com.boostcamp.mapisode.episode.DatabaseRepository
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
	fun provideDatabaseRepository(
		dao: EpisodeDAO,
	): DatabaseRepository {
		return DatabaseRepositoryImpl(dao)
	}
}
