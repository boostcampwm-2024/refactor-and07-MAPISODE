package com.boostcamp.mapisode.episode.di

import com.boostcamp.mapisode.episode.UseCase.UploadNewEpisodeUseCase
import com.boostcamp.mapisode.episode.repository.DatabaseRepository
import com.boostcamp.mapisode.episode.repository.EpisodeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
	@Provides
	@Singleton
	fun provideUploadUseCase(
		episodeRepository: EpisodeRepository,
		databaseRepository: DatabaseRepository,
	): UploadNewEpisodeUseCase {
		return UploadNewEpisodeUseCase(
			episodeRepository,
			databaseRepository,
		)
	}
}
