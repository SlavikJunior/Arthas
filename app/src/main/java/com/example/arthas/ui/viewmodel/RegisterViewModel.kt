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
import com.example.arthas.data.repository.AuthResult
import com.example.arthas.util.ResourceProvider
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false
)

sealed interface RegisterScreenEvent {
    data class EmailChangeEvent(val email: String): RegisterScreenEvent
    data class PasswordChangeEvent(val password: String): RegisterScreenEvent
    data object RegisterClickEvent : RegisterScreenEvent
    data object DialogDismissEvent : RegisterScreenEvent
}

class RegisterViewModel(
    private val resourceProvider: ResourceProvider,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun reduce(event: RegisterScreenEvent) {
        when(event) {
            is RegisterScreenEvent.EmailChangeEvent -> onEmailChange(event.email)
            is RegisterScreenEvent.PasswordChangeEvent -> onPasswordChange(event.password)
            is RegisterScreenEvent.RegisterClickEvent -> onRegisterClick()
            is RegisterScreenEvent.DialogDismissEvent -> onDialogDismiss()
        }
    }

    fun onDialogDismiss() {
        uiState = uiState.copy(error = null)
    }
    private fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    private fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    private fun onRegisterClick() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(error = resourceProvider.getString(R.string.login_error_empty_fields))
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            uiState = when (val result = authRepository.register(uiState.email, uiState.password)) {
                is AuthResult.Success -> uiState.copy(isLoading = false, registrationSuccess = true)
                is AuthResult.Failure -> uiState.copy(isLoading = false, error = result.exceptionMessage)
                is AuthResult.RecoveryPossible -> uiState.copy(isLoading = false, error = resourceProvider.getString(R.string.register_unknown_error))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ArthasApp)
                val authRepository = application.container!!.authRepository
                val resourceProvider = application.container!!.resourceProvider
                RegisterViewModel(resourceProvider = resourceProvider, authRepository = authRepository)
            }
        }
    }
}