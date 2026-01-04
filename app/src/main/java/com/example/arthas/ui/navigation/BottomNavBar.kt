package com.example.arthas.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    object CarsList : BottomNavItem(Routes.CarsList.route, Icons.Default.Home, CARS_LIST_LABEL)
    object Upload : BottomNavItem(Routes.Upload.route, Icons.Default.AddCircle, UPLOAD_LABEL)
    object Profile : BottomNavItem(Routes.Profile.route, Icons.Default.AccountCircle, PROFILE_LABEL)

    companion object {
        private const val CARS_LIST_LABEL = "Автомобили"
        private const val UPLOAD_LABEL = "Добавить"
        private const val PROFILE_LABEL = "Профиль"
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.CarsList,
        BottomNavItem.Upload,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}