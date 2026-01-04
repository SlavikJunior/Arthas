package com.example.arthas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.example.arthas.ui.viewmodel.ProfileScreenEvent
import com.example.arthas.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.user != null) {
                Text(text = "${stringResource(id = R.string.profile_email_label)} ${uiState.user.email}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${stringResource(id = R.string.profile_display_name_label)} ${uiState.user.displayName ?: "-"}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.reduce(ProfileScreenEvent.LogoutEvent) }) {
                    Text(stringResource(id = R.string.profile_logout_button))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.reduce(ProfileScreenEvent.DeleteAccountEvent) }) {
                    Text(stringResource(id = R.string.profile_delete_account_button))
                }
            } else {
                Text(stringResource(id = R.string.profile_error_loading))
            }
        }
    }
}