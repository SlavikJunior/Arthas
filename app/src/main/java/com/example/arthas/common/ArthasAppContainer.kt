package com.example.arthas.common

import android.content.Context
import androidx.room.Room
import com.example.arthas.data.database.ArthasDatabase
import com.example.arthas.data.repository.AuthRepository
import com.example.arthas.data.repository.CarRepository
import com.example.arthas.util.ResourceProvider
import com.example.arthas.util.SessionManager

interface ArthasAppContainer {
    val resourceProvider: ResourceProvider
    val db: ArthasDatabase
    val authRepository: AuthRepository
    val carRepository: CarRepository
    val sessionManager: SessionManager
}

class DefaultArthasAppContainer(
    private val applicationContext: Context
) : ArthasAppContainer {
    override val resourceProvider: ResourceProvider by lazy {
        ResourceProvider(applicationContext)
    }

    override val sessionManager: SessionManager by lazy {
        SessionManager(applicationContext)
    }

    override val db: ArthasDatabase by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = ArthasDatabase::class.java,
            name = ArthasDatabase.ARTHAS_DATABASE_NAME
        ).build()
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(
            userDao = db.userDao(),
            sessionDao = db.sessionDao(),
            resourceProvider = resourceProvider,
            sessionManager = sessionManager
        )
    }

    override val carRepository: CarRepository by lazy {
        CarRepository(db.carDao())
    }
}