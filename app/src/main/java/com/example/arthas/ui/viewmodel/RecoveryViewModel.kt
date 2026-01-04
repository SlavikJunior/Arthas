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

data class RecoveryUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val recoverySuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)

sealed interface RecoveryScreenEvent {
    data class SetEmailEvent(val email: String) : RecoveryScreenEvent
    data object RecoverAccountEvent : RecoveryScreenEvent
    data object DeletePermanentlyEvent : RecoveryScreenEvent
    data object DialogDismissEvent : RecoveryScreenEvent
}

class RecoveryViewModel(
    private val resourceProvider: ResourceProvider,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RecoveryUiState())
        private set

    fun reduce(event: RecoveryScreenEvent) = when (event) {
        is RecoveryScreenEvent.SetEmailEvent -> onEmailSet(event.email)
        is RecoveryScreenEvent.RecoverAccountEvent -> onRecoverAccount()
        is RecoveryScreenEvent.DeletePermanentlyEvent -> onDeletePermanently()
        is RecoveryScreenEvent.DialogDismissEvent -> onDialogDismiss()
    }

    private fun onEmailSet(email: String) {
        uiState = uiState.copy(email = email)
    }

    private fun onRecoverAccount() {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            when (val result = authRepository.recoverAccount(uiState.email)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        recoverySuccess = true
                    )
                }
                is AuthResult.Failure -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.exceptionMessage
                    )
                }
                else -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = resourceProvider.getString(R.string.recovery_error)
                    )
                }
            }
        }
    }

    private fun onDeletePermanently() {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            when (val result = authRepository.deleteAccountPermanently()) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        deleteSuccess = true
                    )
                }
                is AuthResult.Failure -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.exceptionMessage
                    )
                }
                else -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = resourceProvider.getString(R.string.recovery_error)
                    )
                }
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
                RecoveryViewModel(resourceProvider = resourceProvider, authRepository = authRepository)
            }
        }
    }
}