package com.example.arthas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.example.arthas.ui.viewmodel.LoginScreenEvent
import com.example.arthas.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate(Routes.CarsList.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.needsRecovery) {
        if (uiState.needsRecovery && uiState.recoveryEmail.isNotEmpty()) {
            navController.navigate(Routes.Recovery.createRoute(uiState.recoveryEmail))
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.reduce(LoginScreenEvent.DialogDismissEvent)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.login_screen_title)) },
                actions = {
                    TextButton(onClick = { navController.navigate(Routes.Register.route) }) {
                        Text(stringResource(id = R.string.login_register_button))
                    }
                }
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

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { email -> viewModel.reduce(LoginScreenEvent.EmailChangeEvent(email)) },
                label = { Text(stringResource(id = R.string.login_email_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.error != null
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.pass,
                onValueChange = { password -> viewModel.reduce(LoginScreenEvent.PasswordChangeEvent(password)) },
                label = { Text(stringResource(id = R.string.login_password_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.error != null
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.reduce(LoginScreenEvent.LoginClickEvent) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading)
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else
                    Text(stringResource(id = R.string.login_button))
            }
        }
    }
}