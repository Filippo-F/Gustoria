package com.example.gustoria.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = StitchLightPrimary,
    onPrimary = StitchDarkPrimary,
    primaryContainer = StitchDarkPrimary,
    onPrimaryContainer = StitchLightPrimary,

    secondary = StitchLightSecondary,
    onSecondary = StitchDarkSecondary,
    secondaryContainer = StitchDarkSecondary,
    onSecondaryContainer = StitchLightSecondary,

    tertiary = StitchLightTertiary,
    onTertiary = StitchDarkTertiary,
    tertiaryContainer = StitchDarkTertiary,
    onTertiaryContainer = StitchLightTertiary,

    background = Color(0xFF121212), // Standard dark background
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E), // Slightly elevated from background
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFAAAAAA),

    outline = Color.Gray,
    outlineVariant = Color.DarkGray,

    error = Color(0xFFCF6679),
    errorContainer = Color(0xFF370B1E)
)

private val LightColorScheme = lightColorScheme(
    primary = StitchPrimary,
    onPrimary = Color.White,
    primaryContainer = StitchLightPrimary,
    onPrimaryContainer = StitchDarkPrimary,

    secondary = StitchSecondary,
    onSecondary = Color.Black,
    secondaryContainer = StitchLightSecondary,
    onSecondaryContainer = StitchDarkSecondary,

    tertiary = StitchTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = StitchLightTertiary,
    onTertiaryContainer = StitchDarkTertiary,


    background = StitchNeutral,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A), // Almost black (looks much better than raw Color.Black)
    onSurfaceVariant = Color.DarkGray, // A readable gray for subtitles

    outline = Color.LightGray,
    outlineVariant = Color(0xFFE0E0E0), // Slightly softer outline

    error = Color.Red,
    errorContainer = Color(0xFFFDECE8),
)

@Composable
fun GustoriaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}