package com.example.arthas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import com.example.arthas.R
import com.example.arthas.utils.IconResource

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            route = NavRoute.NotificationSettingsScreen.destination,
            label = stringResource(R.string.notification_settings),
            icon = IconResource.fromImageVector(Icons.Default.Settings)
        ),
        BottomNavItem(
            route = NavRoute.NotificationEditScreen.destination,
            label = stringResource(R.string.edit_notification),
            icon = IconResource.fromImageVector(Icons.Default.Edit)
        ),
        BottomNavItem(
            route = NavRoute.MessagesScreen.destination,
            label = stringResource(R.string.messages),
            icon = IconResource.fromImageVector(Icons.Default.Email)
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = item.icon.asPainterResource(),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentDestination?.route == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: IconResource
)