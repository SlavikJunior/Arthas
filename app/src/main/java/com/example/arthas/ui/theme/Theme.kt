package com.example.arthas.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.arthas.ui.theme.ThemeType

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ArthasTheme(
    themeType: ThemeType = ThemeType.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val violetteScheme = lightColorScheme(
        primary = ViolettePrimary,
        secondary = androidx.compose.ui.graphics.Color(0xAE9C5AB2),
        background = androidx.compose.ui.graphics.Color(0xFFC4A8CB),
        surface = androidx.compose.ui.graphics.Color(0xFFC4A8CB)
    )

    val brownScheme = lightColorScheme(
        primary = BrownPrimary,
        secondary = androidx.compose.ui.graphics.Color(0xFF837C4B),
        background = androidx.compose.ui.graphics.Color(0xFFD3CFB8),
        surface = androidx.compose.ui.graphics.Color(0xFFD3CFB8)
    )

    val blueScheme = lightColorScheme(
        primary = BluePrimary,
        secondary = androidx.compose.ui.graphics.Color(0xFF0D47A1),
        background = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
        surface = androidx.compose.ui.graphics.Color(0xFFE3F2FD)
    )

    val colorScheme = when(themeType) {
        ThemeType.VIOLETTE -> violetteScheme
        ThemeType.BROWN -> brownScheme
        ThemeType.BLUE -> blueScheme
        ThemeType.SYSTEM -> when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}