package com.example.arthas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arthas.navigation.NavRoute
import com.example.arthas.screen.FirstScreen
import com.example.arthas.screen.SecondScreen
import com.example.arthas.screen.ThirdScreen
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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavRoute.FirstScreen.destination
                    ) {
                        composable(route = NavRoute.FirstScreen.destination) {
                            FirstScreen(navController)
                        }
                        composable(route = NavRoute.SecondScreen.destination + "/{${Keys.EMAIL_KEY}}") { stackEntry ->
                            val email = stackEntry.arguments?.getString(Keys.EMAIL_KEY) ?: ""
                            SecondScreen(navController, email)
                        }
                        composable(route = NavRoute.ThirdScreen.destination) {
                            ThirdScreen(navController)
                        }
                    }
                }
            }
        }
    }
}