package com.example.arthas.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Dispatchers {
    Default,
    Main,
    Unconfined,
    IO
}

data class MainUiState(
    val currentCountOnSlider: Float = 0.1F,
    val currentDispatchers: Dispatchers = Dispatchers.Default,
    val isSequentially: Boolean = true,
    val isParallel: Boolean = !isSequentially,
    val isDelayedStart: Boolean = false,
    val isBackgroundWork: Boolean = true,
    val progress: Float = 0F,
    val isLoading: Boolean = false
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    var uiState = _uiState.asStateFlow()


}