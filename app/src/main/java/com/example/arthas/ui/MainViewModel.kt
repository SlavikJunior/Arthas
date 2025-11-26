package com.example.arthas.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class Dispatchers {
    Default,
    Main,
    Unconfined,
    IO
}

data class MainUiState(
    val currentCountOnSlider: Float = 0.1F,
    val currentDispatcher: Dispatchers = Dispatchers.Default,
    val isSequentially: Boolean = true,
    val isParallel: Boolean = !isSequentially,
    val isDelayedStart: Boolean = false,
    val isBackgroundWork: Boolean = true,
    val progress: Float = 0F,
    val isLoading: Boolean = false
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        resetSettings()
    }

    fun dispatchersChanged(dispatcher: Dispatchers) {
        _uiState.update { currentState ->
            currentState.copy(currentDispatcher = dispatcher)
        }
    }

    fun pickSequentiallyMode() {
        _uiState.update { currentState ->
            currentState.copy(isSequentially = true)
        }
    }

    fun pickParallelMode() {
        _uiState.update { currentState ->
            currentState.copy(isParallel = true)
        }
    }

    fun pickDelayedMode() {
        _uiState.update { currentState ->
            currentState.copy(isDelayedStart = true)
        }
    }
    fun pickBackgroundWorkMode() {
        _uiState.update { currentState ->
            currentState.copy(isBackgroundWork = true)
        }
    }

    private fun resetSettings() {
        _uiState.value = MainUiState()
    }

    override fun onCleared() {
        super.onCleared()
    }
}