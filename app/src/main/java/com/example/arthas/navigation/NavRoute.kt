package com.example.arthas.navigation

import com.example.arthas.Keys


sealed class NavRoute(val destination: String) {
    object NotificationSettingsScreen: NavRoute(Keys.NOTIFICATION_SETTINGS_SCREEN)
    object NotificationEditScreen: NavRoute(Keys.NOTIFICATION_EDIT_SCREEN)
    object MessagesScreen: NavRoute(Keys.MESSAGES_SCREEN)
}