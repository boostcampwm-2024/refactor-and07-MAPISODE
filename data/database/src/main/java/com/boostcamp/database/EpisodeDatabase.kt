package com.boostcamp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.boostcamp.database.model.EpisodeRoomEntity
import com.boostcamp.database.model.EpisodeRoomEntityConverter

@Database(entities = [EpisodeRoomEntity::class], version = 2)
@TypeConverters(EpisodeRoomEntityConverter::class)
abstract class EpisodeDatabase : RoomDatabase() {
	abstract fun episodeDao(): EpisodeDAO
}
