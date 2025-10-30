package com.example.arthas.navigation

import com.example.arthas.Keys


sealed class NavRoute(val destination: String) {
    object FirstScreen: NavRoute(Keys.FIRST_SCREEN_DESTINATION)
    object SecondScreen: NavRoute(Keys.SECOND_SCREEN_DESTINATION)
    object ThirdScreen: NavRoute(Keys.THIRD_SCREEN_DESTINATION)
}