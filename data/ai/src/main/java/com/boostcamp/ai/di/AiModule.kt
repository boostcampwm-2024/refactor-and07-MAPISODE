package com.boostcamp.ai.di

import android.content.Context
import com.boostcamp.ai.ImageCaptionRepository
import com.boostcamp.ai.LlmRepository
import com.boostcamp.ai.TranslationRepository
import com.boostcamp.ai.imagecaption.ImageCaptionRepositoryImpl
import com.boostcamp.ai.llm.LlmRepositoryImpl
import com.boostcamp.ai.translation.TranslationRepositoryImpl
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
}
