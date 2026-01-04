package com.example.arthas.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.arthas.data.database.ArthasDatabase.Companion.ARTHAS_DATABASE_VERSION
import com.example.arthas.data.database.converter.TimestampAndLongConverter
import com.example.arthas.data.database.dao.CarDao
import com.example.arthas.data.database.dao.SessionDao
import com.example.arthas.data.database.dao.UserDao
import com.example.arthas.data.database.entity.Car
import com.example.arthas.data.database.entity.Session
import com.example.arthas.data.database.entity.User

@Database(
    version = ARTHAS_DATABASE_VERSION,
    entities = [
        User::class,
        Session::class,
        Car::class
    ]
)
@TypeConverters(TimestampAndLongConverter::class)
abstract class ArthasDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun sessionDao(): SessionDao

    abstract fun carDao(): CarDao

    companion object {
        const val ARTHAS_DATABASE_VERSION = 1
        const val ARTHAS_DATABASE_NAME = "arthas_database"
    }
}