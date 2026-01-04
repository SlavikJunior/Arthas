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

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val needsRecovery: Boolean = false,
    val recoveryEmail: String = ""
)

sealed interface LoginScreenEvent {
    data class EmailChangeEvent(val email: String) : LoginScreenEvent
    data class PasswordChangeEvent(val password: String) : LoginScreenEvent
    data object LoginClickEvent : LoginScreenEvent
    data object DialogDismissEvent : LoginScreenEvent
}

class LoginViewModel(
    private val resourceProvider: ResourceProvider,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun reduce(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.EmailChangeEvent -> onEmailChange(event.email)
            is LoginScreenEvent.PasswordChangeEvent -> onPasswordChange(event.password)
            is LoginScreenEvent.LoginClickEvent -> onLoginClick()
            is LoginScreenEvent.DialogDismissEvent -> onDialogDismiss()
        }
    }

    private fun onDialogDismiss() {
        uiState = uiState.copy(error = null)
    }

    private fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    private fun onPasswordChange(password: String) {
        uiState = uiState.copy(pass = password)
    }

    private fun onLoginClick() {
        if (uiState.email.isBlank() || uiState.pass.isBlank()) {
            uiState = uiState.copy(error = resourceProvider.getString(R.string.login_error_empty_fields))
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            when (val result = authRepository.login(uiState.email, uiState.pass)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(isLoading = false, loginSuccess = true)
                }
                is AuthResult.Failure -> {
                    uiState = uiState.copy(isLoading = false, error = result.exceptionMessage)
                }
                is AuthResult.RecoveryPossible -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        needsRecovery = true,
                        recoveryEmail = result.email
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ArthasApp)
                val authRepository = application.container!!.authRepository
                val resourceProvider = application.container!!.resourceProvider
                LoginViewModel(resourceProvider = resourceProvider, authRepository = authRepository)
            }
        }
    }
}