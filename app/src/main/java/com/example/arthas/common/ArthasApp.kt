package com.example.arthas.common

import android.app.Application
import com.example.arthas.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ArthasApp : Application() {

    var container: ArthasAppContainer? = null
        private set

    override fun onCreate() {
        super.onCreate()

        container = DefaultArthasAppContainer(this.applicationContext)

        autoLoginOnStartup()

        cleanupOldSessions()
    }

    private fun autoLoginOnStartup() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                container?.authRepository?.autoLogin()
            } catch (_: Exception) { }
        }
    }

    private fun cleanupOldSessions() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                container?.db?.sessionDao()?.let { sessionDao ->
                    val currentTime = System.currentTimeMillis()
                    val removedSessions = sessionDao.getAllRemovedSessions()

                    for (session in removedSessions) {

                        if (session.removedAt != null) {
                            val daysSinceRemoval = TimeUnit.MILLISECONDS.toDays(
                                currentTime - session.removedAt.time
                            )

                            if (daysSinceRemoval >= AuthRepository.MAX_DAYS_AFTER_REMOVE_BEFORE_DELETE) {
                                sessionDao.markAsDeleted(session.id)
                            }
                        }
                    }
                }
            } catch (_: Exception) { }
        }
    }
}