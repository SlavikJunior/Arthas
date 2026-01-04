package com.example.arthas.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "cars",
    indices = [
        Index("brand", name = "brand_index"),
        Index("model", name = "model_index")
    ]
)
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val brand: String = "",
    val model: String = "",
    @ColumnInfo(name = "year_of_manufacture")
    val yearOfManufacture: Int = 0,
    @ColumnInfo(name = "engine_volume_first_half")
    val engineVolumeFirstHalf: Int? = null, // Целая часть объёма двигателя
    @ColumnInfo(name = "engine_volume_second_half")
    val engineVolumeSecondHalf: Int? = null, // Дробная часть объёма двигателя
    @ColumnInfo(name = "engine_power_hp")
    val enginePowerHP: Int = 0, // Мощность в л.с.
    @ColumnInfo(name = "drive_type")
    val driveType: String = "", // Привод: передний, задний, полный,
    @ColumnInfo(name = "current_market_price")
    val currentMarketPrice: Int = 0, // Текущая рыночная цена
    @ColumnInfo(name = "photo_urls")
    val photoUrl: String? = null
)