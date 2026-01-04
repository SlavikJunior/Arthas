package com.example.arthas.ui.viewmodel

import android.net.Uri
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
import com.example.arthas.data.database.entity.Car
import com.example.arthas.data.repository.CarRepository
import com.example.arthas.util.ResourceProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class UploadUiState(
    val brand: String = "",
    val model: String = "",
    val yearOfManufacture: String = "",
    val engineVolume: String = "",
    val enginePowerHP: String = "",
    val driveType: String = "",
    val currentMarketPrice: String = "",
    val photoUrl: String? = null,
    val isLoading: Boolean = false,
    val uploadProgress: Float = 0f,
    val error: String? = null,
    val uploadSuccess: Boolean = false,
    val selectedFileUri: Uri? = null,
    val selectedFileName: String = ""
)

sealed interface UploadScreenEvent {
    data class BrandChangeEvent(val brand: String) : UploadScreenEvent
    data class ModelChangeEvent(val model: String) : UploadScreenEvent
    data class YearChangeEvent(val year: String) : UploadScreenEvent
    data class EngineVolumeChangeEvent(val volume: String) : UploadScreenEvent
    data class EnginePowerChangeEvent(val power: String) : UploadScreenEvent
    data class DriveTypeChangeEvent(val driveType: String) : UploadScreenEvent
    data class PriceChangeEvent(val price: String) : UploadScreenEvent
    data class FileSelectedEvent(val uri: Uri, val fileName: String) : UploadScreenEvent
    data object UploadClickEvent : UploadScreenEvent
    data object ResetUploadStateEvent : UploadScreenEvent
    data object DialogDismissEvent : UploadScreenEvent
}

class UploadViewModel(
    private val resourceProvider: ResourceProvider,
    private val carRepository: CarRepository
) : ViewModel() {

    var uiState by mutableStateOf(UploadUiState())
        private set

    fun reduce(event: UploadScreenEvent) = when (event) {
        is UploadScreenEvent.BrandChangeEvent -> onBrandChange(event.brand)
        is UploadScreenEvent.ModelChangeEvent -> onModelChange(event.model)
        is UploadScreenEvent.YearChangeEvent -> onYearChange(event.year)
        is UploadScreenEvent.EngineVolumeChangeEvent -> onEngineVolumeChange(event.volume)
        is UploadScreenEvent.EnginePowerChangeEvent -> onEnginePowerChange(event.power)
        is UploadScreenEvent.DriveTypeChangeEvent -> onDriveTypeChange(event.driveType)
        is UploadScreenEvent.PriceChangeEvent -> onPriceChange(event.price)
        is UploadScreenEvent.FileSelectedEvent -> onFileSelected(event.uri, event.fileName)
        is UploadScreenEvent.UploadClickEvent -> onUploadClick()
        is UploadScreenEvent.ResetUploadStateEvent -> onResetUploadState()
        is UploadScreenEvent.DialogDismissEvent -> onDialogDismiss()
    }

    private fun onBrandChange(brand: String) {
        uiState = uiState.copy(brand = brand)
    }

    private fun onModelChange(model: String) {
        uiState = uiState.copy(model = model)
    }

    private fun onYearChange(year: String) {
        uiState = uiState.copy(yearOfManufacture = year)
    }

    private fun onEngineVolumeChange(volume: String) {
        uiState = uiState.copy(engineVolume = volume)
    }

    private fun onEnginePowerChange(power: String) {
        uiState = uiState.copy(enginePowerHP = power)
    }

    private fun onDriveTypeChange(driveType: String) {
        uiState = uiState.copy(driveType = driveType)
    }

    private fun onPriceChange(price: String) {
        uiState = uiState.copy(currentMarketPrice = price)
    }

    private fun onFileSelected(uri: Uri, fileName: String) {
        uiState = uiState.copy(
            selectedFileUri = uri,
            selectedFileName = fileName,
            photoUrl = uri.toString()
        )
    }

    private fun onUploadClick() {
        if (!validateForm()) {
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Симуляция загрузки
                for (progress in 1..100 step 10) {
                    uiState = uiState.copy(uploadProgress = progress.toFloat())
                    delay(100)
                }

                val car = Car(
                    brand = uiState.brand,
                    model = uiState.model,
                    yearOfManufacture = uiState.yearOfManufacture.toIntOrNull() ?: 0,
                    engineVolumeFirstHalf = parseEngineVolumeFirstHalf(uiState.engineVolume),
                    engineVolumeSecondHalf = parseEngineVolumeSecondHalf(uiState.engineVolume),
                    enginePowerHP = uiState.enginePowerHP.toIntOrNull() ?: 0,
                    driveType = uiState.driveType,
                    currentMarketPrice = uiState.currentMarketPrice.toIntOrNull() ?: 0,
                    photoUrl = uiState.photoUrl
                )

                carRepository.insertCar(car)

                uiState = uiState.copy(
                    isLoading = false,
                    uploadSuccess = true,
                    uploadProgress = 100f
                )

                delay(2000)
                onResetUploadState()

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = resourceProvider.getString(R.string.database_error)
                )
            }
        }
    }

    private fun onResetUploadState() {
        uiState = UploadUiState()
    }

    private fun onDialogDismiss() {
        uiState = uiState.copy(error = null)
    }

    private fun validateForm(): Boolean {
        val errors = mutableListOf<String>()
        if (uiState.brand.isBlank()) errors.add(resourceProvider.getString(R.string.upload_screen_error_company_is_blank))
        if (uiState.model.isBlank()) errors.add(resourceProvider.getString(R.string.upload_screen_error_model_is_blank))
        if (uiState.yearOfManufacture.toIntOrNull() == null) errors.add(resourceProvider.getString(R.string.upload_screen_error_year_incorrect))
        if (uiState.enginePowerHP.toIntOrNull() == null) errors.add(resourceProvider.getString(R.string.upload_screen_error_power_incorrect))
        if (uiState.driveType.isBlank()) errors.add(resourceProvider.getString(R.string.upload_screen_error_drivetype_incorrect))
        if (uiState.currentMarketPrice.toIntOrNull() == null) errors.add(resourceProvider.getString(R.string.upload_screen_error_price_incorrect))

        if (errors.isNotEmpty()) {
            uiState = uiState.copy(error = errors.joinToString("\n"))
            return false
        }

        return true
    }

    private fun parseEngineVolumeFirstHalf(volume: String) =
        volume.split(".").firstOrNull()?.toIntOrNull()

    private fun parseEngineVolumeSecondHalf(volume: String) =
        volume.split(".").getOrNull(1)?.toIntOrNull()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ArthasApp)
                val carRepository = application.container!!.carRepository
                val resourceProvider = application.container!!.resourceProvider
                UploadViewModel(carRepository = carRepository, resourceProvider = resourceProvider)
            }
        }
    }
}