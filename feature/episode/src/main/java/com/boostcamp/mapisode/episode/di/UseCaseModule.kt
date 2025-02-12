package com.boostcamp.mapisode.episode.di

import com.boostcamp.mapisode.episode.DatabaseRepository
import com.boostcamp.mapisode.episode.EpisodeRepository
import com.boostcamp.mapisode.episode.ImageCaptionRepository
import com.boostcamp.mapisode.episode.LlmRepository
import com.boostcamp.mapisode.episode.TranslationRepository
import com.boostcamp.mapisode.episode.UploadNewEpisodeUseCase
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
		imageCaptionRepository: ImageCaptionRepository,
		llmRepository: LlmRepository,
		translationRepository: TranslationRepository,
		databaseRepository: DatabaseRepository,
	): UploadNewEpisodeUseCase {
		return UploadNewEpisodeUseCase(
			episodeRepository,
			imageCaptionRepository,
			llmRepository,
			translationRepository,
			databaseRepository,
		)
	}
}
