package com.boostcamp.database.di

import com.boostcamp.database.local.RoomDAO
import com.boostcamp.database.repository.DatabaseRepositoryImpl
import com.boostcamp.mapisode.episode.repository.DatabaseRepository
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
	fun provideDatabaseRepository(roomDao: RoomDAO): DatabaseRepository {
		return DatabaseRepositoryImpl(roomDao)
	}
}
