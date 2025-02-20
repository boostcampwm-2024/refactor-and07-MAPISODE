package com.boostcamp.ai.di

import android.content.Context
import com.boostcamp.ai.imagecaption.ImageCaptionRepositoryImpl
import com.boostcamp.ai.llm.LlmRepositoryImpl
import com.boostcamp.ai.logger.LoggerImpl
import com.boostcamp.ai.objectdetection.ObjectDetectionRepositoryImpl
import com.boostcamp.ai.translation.TranslationRepositoryImpl
import com.boostcamp.mapisode.episode.ImageCaptionRepository
import com.boostcamp.mapisode.episode.LlmRepository
import com.boostcamp.mapisode.episode.Logger
import com.boostcamp.mapisode.episode.ObjectDetectionRepository
import com.boostcamp.mapisode.episode.TranslationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

	@Provides
	@Singleton
	fun provideImageCaptionRepository(@ApplicationContext context: Context): ImageCaptionRepository {
		return ImageCaptionRepositoryImpl(context)
	}

	@Provides
	@Singleton
	fun provideTranslationRepository(): TranslationRepository {
		return TranslationRepositoryImpl()
	}

	@Provides
	@Singleton
	fun provideLlmRepository(@ApplicationContext context: Context): LlmRepository {
		return LlmRepositoryImpl(context)
	}

	@Provides
	@Singleton
	fun provideObjectDetectionRepository(@ApplicationContext context: Context): ObjectDetectionRepository {
		return ObjectDetectionRepositoryImpl(context)
	}

	@Provides
	@Singleton
	fun provideLogger(): Logger {
		return LoggerImpl()
	}
}
