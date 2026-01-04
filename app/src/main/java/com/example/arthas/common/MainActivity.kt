package com.example.arthas.common

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arthas.data.repository.AuthRepository
import com.example.arthas.ui.navigation.BottomNavBar
import com.example.arthas.ui.navigation.Routes
import com.example.arthas.ui.screen.CarsListScreen
import com.example.arthas.ui.screen.LoginScreen
import com.example.arthas.ui.screen.ProfileScreen
import com.example.arthas.ui.screen.RecoveryScreen
import com.example.arthas.ui.screen.RegisterScreen
import com.example.arthas.ui.screen.UploadScreen
import com.example.arthas.ui.theme.ArthasTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArthasTheme {
                MainApp(application = this@MainActivity.application as ArthasApp)
            }
        }
    }
}

@Composable
fun MainApp(application: ArthasApp) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = when (currentRoute) {
        Routes.CarsList.route, Routes.Upload.route, Routes.Profile.route -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            application = application,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    application: ArthasApp,
    modifier: Modifier = Modifier,
) {
    var isLoading by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf(Routes.Login.route) }

    LaunchedEffect(Unit) {
        delay(500)

        val authRepository = application.container?.authRepository

        if (authRepository != null && AuthRepository.getCurrentUser() != null) {
            startDestination = Routes.CarsList.route
        } else {
            authRepository?.autoLogin()
            delay(500)

            startDestination = if (AuthRepository.getCurrentUser() != null) {
                Routes.CarsList.route
            } else {
                Routes.Login.route
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(Routes.Register.route) {
                RegisterScreen(navController = navController)
            }
            composable(Routes.Login.route) {
                LoginScreen(navController = navController)
            }
            composable(Routes.Recovery.route) { backStackEntry ->
                val email = backStackEntry.arguments?.getString(Routes.Recovery.Args.EMAIL) ?: ""
                RecoveryScreen(
                    navController = navController,
                    email = email
                )
            }
            composable(Routes.CarsList.route) {
                CarsListScreen(navController = navController)
            }
            composable(Routes.Upload.route) {
                UploadScreen(navController = navController)
            }
            composable(Routes.Profile.route) {
                ProfileScreen(navController = navController)
            }
        }
    }
}