package com.example.arthas.ui.theme

import androidx.compose.runtime.mutableStateOf

object ThemeController {
    var currentTheme = mutableStateOf(ThemeType.SYSTEM)
}