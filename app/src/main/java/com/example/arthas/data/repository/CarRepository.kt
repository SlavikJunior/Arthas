package com.example.arthas.data.repository

import com.example.arthas.data.database.dao.CarDao
import com.example.arthas.data.database.entity.Car
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CarRepository(private val carDao: CarDao) {

    fun getAllCars(): Flow<List<Car>> = carDao.getAllCars()

    suspend fun insertCar(car: Car) = carDao.insert(car)

    fun getCarsSortedByYearAsc(): Flow<List<Car>> =
        getAllCars().map { cars -> cars.sortedBy { it.yearOfManufacture } }

    fun getCarsSortedByYearDesc(): Flow<List<Car>> =
        getAllCars().map { cars -> cars.sortedByDescending { it.yearOfManufacture } }

    fun getCarsSortedByPriceAsc(): Flow<List<Car>> =
        getAllCars().map { cars -> cars.sortedBy { it.currentMarketPrice } }

    fun getCarsSortedByPriceDesc(): Flow<List<Car>> =
        getAllCars().map { cars -> cars.sortedByDescending { it.currentMarketPrice } }

    fun getCarsSortedByPowerAsc(): Flow<List<Car>> =
        getAllCars().map { cars -> cars.sortedBy { it.enginePowerHP } }

    fun getCarsSortedByPowerDesc(): Flow<List<Car>> =
        getAllCars().map { cars -> cars.sortedByDescending { it.enginePowerHP } }
}