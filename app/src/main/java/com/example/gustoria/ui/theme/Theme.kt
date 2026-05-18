package com.example.gustoria.ui.theme

import android.os.Build
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
    primary = StitchDarkPrimary,
    secondary = StitchDarkSecondary,
    tertiary = StitchTertiary
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
    onSurface = Color.Gray,
    onSurfaceVariant = Color.LightGray,

    outline = Color.LightGray,
    outlineVariant = Color.LightGray,

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
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}