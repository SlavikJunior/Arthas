package com.example.arthas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arthas.ResetSettingsException
import com.example.arthas.ShowSnackbarException
import com.example.arthas.ShowToastException
import com.example.arthas.computation
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val isLoading: Boolean = false,
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private var computationJobs: List<Job> = emptyList()
    private var computationJob: Job? = null
    private var stoppedCoroutineCount: Int = 0

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            when (throwable) {
                is ShowToastException -> _toastMessage.emit(TOAST_EXCEPTION)
                is ShowSnackbarException -> _snackbarMessage.emit(SNACKBAR_EXCEPTION)
                is ResetSettingsException -> resetSettings()
            }
        }
    }

    fun changeDispatcher(dispatcher: Dispatchers) {
        _uiState.update { it.copy(currentDispatcher = dispatcher) }
    }

    fun changeSequentiallyMode() {
        _uiState.update { 
            it.copy(isSequentially = !it.isSequentially, isParallel = !it.isParallel) 
        }
    }

    fun changeParallelMode() = changeSequentiallyMode()

    fun changeDelayedMode(isDelayed: Boolean) {
        _uiState.update { it.copy(isDelayedStart = isDelayed) }
    }

    fun changeBackgroundWorkMode(isBackgroundWork: Boolean) {
        _uiState.update { it.copy(isBackgroundWork = isBackgroundWork) }
    }

    fun changeCoroutinesCount(currentCountOnSlider: Float) {
        _uiState.update { it.copy(coroutinesCount = currentCountOnSlider) }
    }

    fun startComputation(isRestart: Boolean = false) {
        val coroutineCount = if (isRestart) stoppedCoroutineCount else _uiState.value.coroutinesCount.toInt()
        if (coroutineCount == 0) return

        computationJob = viewModelScope.launch(coroutineExceptionHandler) {
            computation(
                countOfCoroutines = coroutineCount,
                scope = this,
                dispatcher = when (_uiState.value.currentDispatcher) {
                    Dispatchers.Default -> kotlinx.coroutines.Dispatchers.Default
                    Dispatchers.Main -> kotlinx.coroutines.Dispatchers.Main
                    Dispatchers.Unconfined -> kotlinx.coroutines.Dispatchers.Unconfined
                    Dispatchers.IO -> kotlinx.coroutines.Dispatchers.IO
                },
                isSequentially = _uiState.value.isSequentially,
                isDelayedStart = _uiState.value.isDelayedStart,
                onProgress = { progress -> _uiState.update { it.copy(progress = progress) } },
                onLoadingChange = { isLoading -> _uiState.update { it.copy(isLoading = isLoading) } },
                onJobsCreated = { jobs -> computationJobs = jobs },
                onSuccess = {
                    viewModelScope.launch { _toastMessage.emit(ON_SUCCESS) }
                }
            )
        }
    }

    fun cancelComputation() {
        val cancelledCount = computationJobs.count { it.isActive }
        computationJobs.forEach { it.cancel() }
        computationJob?.cancel()
        _uiState.update { it.copy(isLoading = false) }
        viewModelScope.launch {
            _toastMessage.emit(ON_CANCELL.format(cancelledCount))
        }
    }

    fun onAppStop() {
        if (!_uiState.value.isBackgroundWork) {
            stoppedCoroutineCount = computationJobs.count { it.isActive }
            cancelComputation()
        }
    }

    fun onAppStart() {
        if (!_uiState.value.isBackgroundWork && stoppedCoroutineCount > 0) {
            startComputation(isRestart = true)
            stoppedCoroutineCount = 0
        }
    }

    private fun resetSettings() {
        _uiState.value = MainUiState()
    }

    companion object {
        private const val TOAST_EXCEPTION = "Toast Exception"
        private const val SNACKBAR_EXCEPTION = "Snackbar Exception"
        private const val ON_SUCCESS = "All coroutines finished successfully!"
        private const val ON_CANCELL = "%d coroutines were cancelled"
    }
}