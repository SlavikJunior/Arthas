package com.example.arthas.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arthas.data.database.entity.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Insert(entity = Car::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(car: Car)

    @Query("select * from $TABLE_NAME")
    fun getAllCars(): Flow<List<Car>>

    companion object {
        const val TABLE_NAME = "cars"
    }
}