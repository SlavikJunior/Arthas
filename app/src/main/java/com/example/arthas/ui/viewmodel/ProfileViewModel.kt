package com.example.arthas.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.arthas.R
import com.example.arthas.common.ArthasApp
import com.example.arthas.data.repository.AuthRepository
import com.example.arthas.util.ResourceProvider
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false,
    val user: UserProfile? = null
)

data class UserProfile(
    val email: String,
    val displayName: String? = null
)

sealed interface ProfileScreenEvent {
    data object LogoutEvent : ProfileScreenEvent
    data object DeleteAccountEvent : ProfileScreenEvent
    data object DialogDismissEvent : ProfileScreenEvent
}

class ProfileViewModel(
    private val resourceProvider: ResourceProvider,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState(isLoading = true))
        private set

    init {
        loadUserProfile()
    }

    fun reduce(event: ProfileScreenEvent) = when (event) {
        is ProfileScreenEvent.LogoutEvent -> onLogout()
        is ProfileScreenEvent.DeleteAccountEvent -> onDeleteAccount()
        is ProfileScreenEvent.DialogDismissEvent -> onDialogDismiss()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val currentUser = AuthRepository.getCurrentUser()
                uiState = if (currentUser != null) {
                    ProfileUiState(
                        user = UserProfile(
                            email = currentUser.email,
                            displayName = currentUser.email.substringBefore("@")
                        )
                    )
                } else {
                    ProfileUiState(error = resourceProvider.getString(R.string.profile_error_user_not_found))
                }
            } catch (_: Exception) {
                uiState = ProfileUiState(error = resourceProvider.getString(R.string.profile_error_loading))
            }
        }
    }

    private fun onLogout() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                authRepository.logout()
                uiState = uiState.copy(isLoading = false, loggedOut = true)
            } catch (_: Exception) {
                uiState = uiState.copy(isLoading = false, error = resourceProvider.getString(R.string.profile_error_logout))
            }
        }
    }

    private fun onDeleteAccount() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                authRepository.deleteAccount()
                uiState = uiState.copy(isLoading = false, loggedOut = true)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: resourceProvider.getString(R.string.database_error)
                )
            }
        }
    }

    private fun onDialogDismiss() {
        uiState = uiState.copy(error = null)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ArthasApp)
                val authRepository = application.container!!.authRepository
                val resourceProvider = application.container!!.resourceProvider
                ProfileViewModel(resourceProvider = resourceProvider, authRepository = authRepository)
            }
        }
    }
}