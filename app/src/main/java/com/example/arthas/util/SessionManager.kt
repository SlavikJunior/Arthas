package com.example.arthas.util

import android.content.Context
import androidx.core.content.edit
import com.example.arthas.data.database.entity.User

class SessionManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(user: User) {
        sharedPreferences.edit {
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USER_EMAIL, user.email)
            user.sessionId?.let { putInt(KEY_SESSION_ID, it) }
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_LAST_LOGIN_TIME, System.currentTimeMillis())
        }
    }

    fun getCurrentUserId() = if (sharedPreferences.getInt(KEY_USER_ID, -1) != -1) sharedPreferences.getInt(KEY_USER_ID, -1) else null

    fun clearSession() {
        sharedPreferences.edit {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_SESSION_ID)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_LAST_LOGIN_TIME)
        }
    }

    companion object {
        private const val PREF_NAME = "ArthasSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_LAST_LOGIN_TIME = "last_login_time"
    }
}