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
import com.example.arthas.data.database.entity.Car
import com.example.arthas.data.repository.CarRepository
import com.example.arthas.util.ResourceProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CarsListUiState(
    val cars: List<Car> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSortBottomSheetVisible: Boolean = false,
    val currentSortType: SortType = SortType.NONE
)

enum class SortType {
    NONE,
    YEAR_ASC,
    YEAR_DESC,
    PRICE_ASC,
    PRICE_DESC,
    POWER_ASC,
    POWER_DESC
}

sealed interface CarsListScreenEvent {
    data object LoadCarsEvent : CarsListScreenEvent
    data object ShowSortBottomSheetEvent : CarsListScreenEvent
    data object HideSortBottomSheetEvent : CarsListScreenEvent
    data class SortCarsEvent(val sortType: SortType) : CarsListScreenEvent
    data object DialogDismissEvent : CarsListScreenEvent
}

class CarsListViewModel(
    private val resourceProvider: ResourceProvider,
    private val carRepository: CarRepository
) : ViewModel() {

    var uiState by mutableStateOf(CarsListUiState(isLoading = true))
        private set

    init {
        loadCars()
    }

    fun reduce(event: CarsListScreenEvent) = when (event) {
        is CarsListScreenEvent.LoadCarsEvent -> loadCars()
        is CarsListScreenEvent.ShowSortBottomSheetEvent -> showSortBottomSheet()
        is CarsListScreenEvent.HideSortBottomSheetEvent -> hideSortBottomSheet()
        is CarsListScreenEvent.SortCarsEvent -> sortCars(event.sortType)
        is CarsListScreenEvent.DialogDismissEvent -> onDialogDismiss()
    }

    private fun loadCars() {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                carRepository.getAllCars().collectLatest { cars ->
                    uiState = uiState.copy(
                        cars = cars,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = resourceProvider.getString(R.string.database_error)
                )
            }
        }
    }

    private fun showSortBottomSheet() {
        uiState = uiState.copy(isSortBottomSheetVisible = true)
    }

    private fun hideSortBottomSheet() {
        uiState = uiState.copy(isSortBottomSheetVisible = false)
    }

    private fun sortCars(sortType: SortType) {
        uiState = uiState.copy(isLoading = true, error = null, currentSortType = sortType)

        viewModelScope.launch {
            try {
                val flow = when (sortType) {
                    SortType.NONE -> carRepository.getAllCars()
                    SortType.YEAR_ASC -> carRepository.getCarsSortedByYearAsc()
                    SortType.YEAR_DESC -> carRepository.getCarsSortedByYearDesc()
                    SortType.PRICE_ASC -> carRepository.getCarsSortedByPriceAsc()
                    SortType.PRICE_DESC -> carRepository.getCarsSortedByPriceDesc()
                    SortType.POWER_ASC -> carRepository.getCarsSortedByPowerAsc()
                    SortType.POWER_DESC -> carRepository.getCarsSortedByPowerDesc()
                }

                flow.collectLatest { cars ->
                    uiState = uiState.copy(
                        cars = cars,
                        isLoading = false,
                        isSortBottomSheetVisible = false
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = resourceProvider.getString(R.string.database_error)
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
                val carRepository = application.container!!.carRepository
                val resourceProvider = application.container!!.resourceProvider
                CarsListViewModel(
                    resourceProvider = resourceProvider,
                    carRepository = carRepository
                )
            }
        }
    }
}