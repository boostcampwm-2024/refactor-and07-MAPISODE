package com.boostcamp.database.di

import android.content.Context
import androidx.room.Room
import com.boostcamp.database.local.RoomDAO
import com.boostcamp.database.local.RoomDatabase
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
	fun provideEpisodeDB(@ApplicationContext context: Context): RoomDatabase = Room.databaseBuilder(
		context = context,
		klass = RoomDatabase::class.java,
		name = "episode.db",
	).build()

	@Provides
	@Singleton
	fun provideEpisodeDAO(db: RoomDatabase): RoomDAO = db.roomDao()
}
