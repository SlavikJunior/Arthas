package com.example.arthas.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.arthas.R
import com.example.arthas.ui.navigation.Routes
import com.example.arthas.ui.viewmodel.UploadScreenEvent
import com.example.arthas.ui.viewmodel.UploadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    navController: NavHostController,
    viewModel: UploadViewModel = viewModel(factory = UploadViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.uploadSuccess) {
        if (uiState.uploadSuccess) {
            navController.navigate(Routes.CarsList.route) {
                popUpTo(Routes.CarsList.route)
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val fileName = it.pathSegments.lastOrNull() ?: ""
                viewModel.reduce(UploadScreenEvent.FileSelectedEvent(it, fileName))
            }
        }
    )

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.upload_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = uiState.brand,
                onValueChange = { viewModel.reduce(UploadScreenEvent.BrandChangeEvent(it)) },
                label = { Text(stringResource(id = R.string.upload_brand_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.model,
                onValueChange = { viewModel.reduce(UploadScreenEvent.ModelChangeEvent(it)) },
                label = { Text(stringResource(id = R.string.upload_model_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.yearOfManufacture,
                onValueChange = { viewModel.reduce(UploadScreenEvent.YearChangeEvent(it)) },
                label = { Text(stringResource(id = R.string.upload_year_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.engineVolume,
                onValueChange = { viewModel.reduce(UploadScreenEvent.EngineVolumeChangeEvent(it)) },
                label = { Text(stringResource(id = R.string.upload_engine_label)) },
                placeholder = { Text(stringResource(id = R.string.upload_engine_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.enginePowerHP,
                onValueChange = { viewModel.reduce(UploadScreenEvent.EnginePowerChangeEvent(it)) },
                label = { Text(stringResource(id = R.string.upload_power_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.driveType,
                onValueChange = { viewModel.reduce(UploadScreenEvent.DriveTypeChangeEvent(it)) },
                label = { Text(stringResource(id = R.string.upload_drive_label)) },
                placeholder = { Text(stringResource(id = R.string.upload_drive_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.currentMarketPrice,
                onValueChange = { viewModel.reduce(UploadScreenEvent.PriceChangeEvent(it)) },
                label = { Text(stringResource(id = R.string.upload_price_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { filePickerLauncher.launch("image/*") }) {
                Text(stringResource(id = R.string.upload_select_photo_button))
            }

            if (uiState.selectedFileName.isNotEmpty()) {
                Text("${stringResource(id = R.string.upload_selected_file_label)} ${uiState.selectedFileName}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.upload_uploading))
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = uiState.uploadProgress / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = { viewModel.reduce(UploadScreenEvent.UploadClickEvent) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.upload_button_text))
            }

            if (uiState.uploadSuccess) {
                Text(stringResource(id = R.string.upload_success))
                LaunchedEffect(Unit) {
                    viewModel.reduce(UploadScreenEvent.ResetUploadStateEvent)
                }
            }
        }
    }
}