package com.example.arthas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.arthas.navigation.BottomNavigationBar
import com.example.arthas.navigation.NavRoute
import com.example.arthas.screen.MessagesScreen
import com.example.arthas.screen.NotificationEditScreen
import com.example.arthas.screen.NotificationSettingsScreen
import com.example.arthas.ui.theme.ArthasTheme
import com.example.arthas.ui.theme.ThemeController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentThemeType by ThemeController.currentTheme

            ArthasTheme(themeType = currentThemeType) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentDestination = currentDestination,
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.NotificationSettingsScreen.destination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = NavRoute.NotificationSettingsScreen.destination) {
                NotificationSettingsScreen()
            }
            composable(route = NavRoute.NotificationEditScreen.destination) {
                NotificationEditScreen()
            }
            composable(route = NavRoute.MessagesScreen.destination) {
                MessagesScreen()
            }
        }
    }
}