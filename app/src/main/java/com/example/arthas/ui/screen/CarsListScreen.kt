package com.example.arthas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.arthas.R
import com.example.arthas.data.database.entity.Car
import com.example.arthas.ui.navigation.Routes
import com.example.arthas.ui.viewmodel.CarsListScreenEvent
import com.example.arthas.ui.viewmodel.CarsListViewModel
import com.example.arthas.ui.viewmodel.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsListScreen(
    navController: NavHostController,
    viewModel: CarsListViewModel = viewModel(factory = CarsListViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val modalBottomSheetState = rememberModalBottomSheetState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.reduce(CarsListScreenEvent.DialogDismissEvent)
        }
    }

    if (uiState.isSortBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.reduce(CarsListScreenEvent.HideSortBottomSheetEvent) },
            sheetState = modalBottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.cars_list_sort_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SortOptionItem(
                    text = stringResource(R.string.cars_list_sort_none),
                    isSelected = uiState.currentSortType == SortType.NONE,
                    onClick = {
                        viewModel.reduce(CarsListScreenEvent.SortCarsEvent(SortType.NONE))
                    }
                )

                SortOptionItem(
                    text = stringResource(R.string.cars_list_sort_year_asc),
                    isSelected = uiState.currentSortType == SortType.YEAR_ASC,
                    onClick = {
                        viewModel.reduce(CarsListScreenEvent.SortCarsEvent(SortType.YEAR_ASC))
                    }
                )

                SortOptionItem(
                    text = stringResource(R.string.cars_list_sort_year_desc),
                    isSelected = uiState.currentSortType == SortType.YEAR_DESC,
                    onClick = {
                        viewModel.reduce(CarsListScreenEvent.SortCarsEvent(SortType.YEAR_DESC))
                    }
                )

                SortOptionItem(
                    text = stringResource(R.string.cars_list_sort_price_asc),
                    isSelected = uiState.currentSortType == SortType.PRICE_ASC,
                    onClick = {
                        viewModel.reduce(CarsListScreenEvent.SortCarsEvent(SortType.PRICE_ASC))
                    }
                )

                SortOptionItem(
                    text = stringResource(R.string.cars_list_sort_price_desc),
                    isSelected = uiState.currentSortType == SortType.PRICE_DESC,
                    onClick = {
                        viewModel.reduce(CarsListScreenEvent.SortCarsEvent(SortType.PRICE_DESC))
                    }
                )

                SortOptionItem(
                    text = stringResource(R.string.cars_list_sort_power_asc),
                    isSelected = uiState.currentSortType == SortType.POWER_ASC,
                    onClick = {
                        viewModel.reduce(CarsListScreenEvent.SortCarsEvent(SortType.POWER_ASC))
                    }
                )

                SortOptionItem(
                    text = stringResource(R.string.cars_list_sort_power_desc),
                    isSelected = uiState.currentSortType == SortType.POWER_DESC,
                    onClick = {
                        viewModel.reduce(CarsListScreenEvent.SortCarsEvent(SortType.POWER_DESC))
                    }
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cars_list_screen_title)) },
                actions = {
                    IconButton(
                        onClick = { viewModel.reduce(CarsListScreenEvent.ShowSortBottomSheetEvent) }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.cars_list_sort_button))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Routes.Upload.route)
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(id = R.string.cars_list_add_car_button)) }
            )
        }
    ) { paddings ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.cars.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.cars_list_empty),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("upload") }
                    ) {
                        Text(stringResource(id = R.string.cars_list_add_first_car))
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.cars) { car ->
                    CarCard(car = car)
                }
            }
        }
    }
}

@Composable
fun SortOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun CarCard(car: Car) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${car.brand} ${car.model}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.cars_list_year_label),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = car.yearOfManufacture.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.cars_list_engine_label),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = buildString {
                            val first = car.engineVolumeFirstHalf ?: 0
                            val second = car.engineVolumeSecondHalf ?: 0
                            append("$first.$second ${stringResource(id = R.string.cars_list_engine_volume_unit)}")
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.cars_list_power_label),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "${car.enginePowerHP} ${stringResource(id = R.string.cars_list_power_unit)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.cars_list_drive_label),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = car.driveType,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.cars_list_price_label),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "${car.currentMarketPrice} ${stringResource(id = R.string.cars_list_price_unit)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}