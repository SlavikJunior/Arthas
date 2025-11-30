package com.example.arthas.ui

import androidx.lifecycle.ViewModel
import com.example.arthas.computation
import kotlinx.coroutines.CoroutineScope
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
    val coroutinesCount: Float = 10F,
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

    fun changeDispatcher(dispatcher: Dispatchers) {
        _uiState.update { currentState ->
            currentState.copy(currentDispatcher = dispatcher)
        }
    }

    fun changeSequentiallyMode() {
        _uiState.update { currentState ->
            currentState.copy(
                isSequentially = !currentState.isSequentially,
                isParallel = !currentState.isParallel
            )
        }
    }

    fun changeParallelMode() =
        changeSequentiallyMode()
    fun changeDelayedMode(isDelayed: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isDelayedStart = isDelayed)
        }
    }
    fun changeBackgroundWorkMode(isBackgroundWork: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isBackgroundWork = isBackgroundWork)
        }
    }

    fun changeCoroutinesCount(currentCountOnSlider: Float) {
        _uiState.update { currentState ->
            currentState.copy(coroutinesCount = currentCountOnSlider)
        }
    }

    fun startComputation(scope: CoroutineScope) =
        computation(
            countOfCoroutines = _uiState.value.coroutinesCount.toInt(),
            scope = scope,
            dispatcher = when(_uiState.value.currentDispatcher) {
                Dispatchers.Default -> kotlinx.coroutines.Dispatchers.Default
                Dispatchers.Main -> kotlinx.coroutines.Dispatchers.Main
                Dispatchers.Unconfined -> kotlinx.coroutines.Dispatchers.Unconfined
                Dispatchers.IO -> kotlinx.coroutines.Dispatchers.IO
            },
            isSequentially = _uiState.value.isSequentially
        )

    private fun resetSettings() {
        _uiState.value = MainUiState()
    }

    override fun onCleared() {
        super.onCleared()
    }
}