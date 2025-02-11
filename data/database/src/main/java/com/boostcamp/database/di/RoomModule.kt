package com.boostcamp.database.di

import android.content.Context
import androidx.room.Room
import com.boostcamp.database.EpisodeDAO
import com.boostcamp.database.EpisodeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
	@Provides
	@Singleton
	fun provideEpisodeDB(@ApplicationContext context: Context): EpisodeDatabase = Room.databaseBuilder(
		context = context,
		klass = EpisodeDatabase::class.java,
		name = "episode.db"
	).build()

	@Provides
	@Singleton
	fun provideEpisodeDAO(db: EpisodeDatabase): EpisodeDAO = db.episodeDao()
}
