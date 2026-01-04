package com.example.arthas.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import com.example.arthas.ui.navigation.Routes
import com.example.arthas.ui.viewmodel.RecoveryScreenEvent
import com.example.arthas.ui.viewmodel.RecoveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(
    navController: NavHostController,
    email: String,
    viewModel: RecoveryViewModel = viewModel(factory = RecoveryViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.reduce(RecoveryScreenEvent.SetEmailEvent(email))
    }

    LaunchedEffect(uiState.recoverySuccess) {
        if (uiState.recoverySuccess) {
            navController.navigate(Routes.CarsList.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.recovery_screen_title)) }
            )
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.recovery_message),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Email: $email",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.reduce(RecoveryScreenEvent.RecoverAccountEvent) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(id = R.string.recovery_recover_button))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.reduce(RecoveryScreenEvent.DeletePermanentlyEvent) },
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.recovery_delete_permanently_button))
            }
        }
    }
}