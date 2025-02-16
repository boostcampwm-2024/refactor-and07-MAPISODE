package com.boostcamp.database.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.boostcamp.database.model.EpisodeRoomEntity
import com.boostcamp.database.model.GroupRoomEntity
import com.boostcamp.database.model.RoomEntityConverter

@Database(entities = [EpisodeRoomEntity::class, GroupRoomEntity::class], version = 3)
@TypeConverters(RoomEntityConverter::class)
abstract class RoomDatabase : RoomDatabase() {
	abstract fun roomDao(): RoomDAO
}
