package com.example.spacex.Theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val SpaceXRed = Color(0xFFA42D25)
val DarkBackground = Color(0xFF121212)
val LightBackground = Color(0xFFF5F5F5)
val SurfaceColor = Color.White

private val LightColorScheme = lightColorScheme(
    primary = SpaceXRed,
    onPrimary = Color.White,
    background = LightBackground,
    surface = SurfaceColor,
    onSurface = Color.Black,
    primaryContainer = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = SpaceXRed,
    onPrimary = Color.White,
    background = DarkBackground,
    surface = Color.DarkGray,
    onSurface = Color.White,
    primaryContainer = Color.Black
)

data class AppThemeState(
    val isDarkTheme: Boolean
)

val LocalAppTheme = staticCompositionLocalOf { AppThemeState(isDarkTheme = false) }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppTheme provides AppThemeState(darkTheme)) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}