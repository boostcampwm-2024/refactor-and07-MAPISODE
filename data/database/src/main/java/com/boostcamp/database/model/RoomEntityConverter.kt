package com.boostcamp.database.model

import androidx.room.TypeConverter
import java.util.Date

class RoomEntityConverter {

	@TypeConverter
	fun fromPairToString(pair: Pair<Double, Double>): String {
		return "${pair.first},${pair.second}"
	}

	@TypeConverter
	fun fromStringToPair(value: String): Pair<Double, Double> {
		val (first, second) = value.split(",")
		return Pair(first.toDouble(), second.toDouble())
	}

	@TypeConverter
	fun fromDateToLong(date: Date): Long {
		return date.time
	}

	@TypeConverter
	fun fromLongToDate(value: Long): Date {
		return Date(value)
	}
}
