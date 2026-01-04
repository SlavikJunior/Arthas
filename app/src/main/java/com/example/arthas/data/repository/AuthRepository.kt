package com.example.arthas.data.repository

import com.example.arthas.R
import com.example.arthas.data.database.dao.SessionDao
import com.example.arthas.data.database.dao.UserDao
import com.example.arthas.data.database.entity.Session
import com.example.arthas.data.database.entity.User
import com.example.arthas.util.Argon2Hasher
import com.example.arthas.util.ResourceProvider
import com.example.arthas.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

sealed interface AuthResult {
    data object Success : AuthResult
    data class Failure(val exceptionMessage: String) : AuthResult
    data class RecoveryPossible(val email: String) : AuthResult
}

class AuthRepository(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
    private val resourceProvider: ResourceProvider,
    private val sessionManager: SessionManager
) {

    private val argon2Hasher = Argon2Hasher()

    suspend fun register(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val passwordHash = argon2Hasher.hash(password)
            val sessionId = sessionDao.insert(Session())
            val user = User(
                sessionId = sessionId.toInt(),
                email = email,
                passwordHash = passwordHash
            )
            userDao.insert(user)
            setCurrentUser(user)
            sessionManager.saveSession(user)
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: resourceProvider.getString(R.string.database_error))
        }
    }

    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val user = userDao.getUserByEmail(email)
                ?: return@withContext AuthResult.Failure(resourceProvider.getString(R.string.login_error_empty_fields))

            if (!argon2Hasher.verify(password, user.passwordHash)) {
                return@withContext AuthResult.Failure(resourceProvider.getString(R.string.login_error_empty_fields))
            }

            val session = user.sessionId?.let { sessionDao.getSessionById(it) }

            if (session?.removedAt != null) {
                val daysSinceRemoval = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - session.removedAt.time)
                if (daysSinceRemoval < MAX_DAYS_AFTER_REMOVE_BEFORE_DELETE) {
                    return@withContext AuthResult.RecoveryPossible(email)
                } else {
                    return@withContext AuthResult.Failure(resourceProvider.getString(R.string.profile_error_user_deleted_permanently))
                }
            }

            setCurrentUser(user)
            sessionManager.saveSession(user)
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: resourceProvider.getString(R.string.database_error))
        }
    }

    suspend fun autoLogin(): AuthResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = sessionManager.getCurrentUserId()
                ?: return@withContext AuthResult.Failure(resourceProvider.getString(R.string.auth_error_no_saved_session))

            val user = userDao.getUserWithValidSession(userId)

            user?.let {
                setCurrentUser(it)
                AuthResult.Success
            } ?: run {
                sessionManager.clearSession()
                AuthResult.Failure(resourceProvider.getString(R.string.auth_error_session_expired))
            }
        } catch (e: Exception) {
            sessionManager.clearSession()
            AuthResult.Failure(e.message ?: resourceProvider.getString(R.string.auth_error_auto_login_failed))
        }
    }

    suspend fun deleteAccount() = withContext(Dispatchers.IO) {
        val user = getCurrentUser() ?: return@withContext
        user.sessionId?.let {
            sessionDao.removeSessionByUser(it)
        }
        sessionManager.clearSession()
        logout()
    }

    suspend fun deleteAccountPermanently(): AuthResult {
        val user = getCurrentUser()
            ?: return AuthResult.Failure(resourceProvider.getString(R.string.auth_error_user_not_found))

        return try {
            userDao.delete(user)
            user.sessionId?.let { sessionId ->
                sessionDao.softDelete(sessionId)
            }
            sessionManager.clearSession()
            logout()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: resourceProvider.getString(R.string.auth_error_deletion_failed))
        }
    }

    suspend fun recoverAccount(email: String): AuthResult = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email)
        if (user?.sessionId != null) {
            sessionDao.recoverSession(user.sessionId)
            setCurrentUser(user)
            sessionManager.saveSession(user)
            return@withContext AuthResult.Success
        }
        return@withContext AuthResult.Failure(resourceProvider.getString(R.string.database_error))
    }

    suspend fun logout() {
        setCurrentUser(null)
        sessionManager.clearSession()
    }

    companion object {

        const val MAX_DAYS_AFTER_REMOVE_BEFORE_DELETE = 7
        private var currentUser: User? = null

        fun getCurrentUser() = currentUser?.copy()

        fun setCurrentUser(user: User? = null) {
            currentUser = user
        }
    }
}