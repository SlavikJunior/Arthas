package com.example.arthas.data.database.converter

import androidx.room.TypeConverter
import java.sql.Timestamp

class TimestampAndLongConverter {

    @TypeConverter
    fun fromLongToTimestamp(value: Long?) = value?.let { Timestamp(it) }

    @TypeConverter
    fun fromTimestampToLong(timestamp: Timestamp?) = timestamp?.time
}