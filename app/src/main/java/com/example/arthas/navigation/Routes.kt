package com.example.arthas.navigation

import com.example.arthas.Keys


sealed class NavRoute(val destination: String) {
    object SecondScreen: NavRoute(Keys.SECOND_SCREEN_DESTINATION)
    object SettingsScreen: NavRoute(Keys.THIRD_SCREEN_DESTINATION)
}