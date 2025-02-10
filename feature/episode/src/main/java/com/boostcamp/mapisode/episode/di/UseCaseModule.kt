package com.boostcamp.mapisode.episode.di

import com.boostcamp.ai.EpisodeGenerationUseCase
import com.boostcamp.ai.ImageCaptionRepository
import com.boostcamp.ai.LlmRepository
import com.boostcamp.ai.TranslationRepository
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
	fun provideAiUseCase(
		imageCaptionRepository: ImageCaptionRepository,
		llmRepository: LlmRepository,
		translationRepository: TranslationRepository,
	): EpisodeGenerationUseCase {
		return EpisodeGenerationUseCase(
			imageCaptionRepository,
			llmRepository,
			translationRepository,
		)
	}
}
