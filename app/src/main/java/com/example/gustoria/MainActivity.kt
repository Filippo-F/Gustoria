package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.ui.navigation.GustoriaApp
import com.example.gustoria.ui.navigation.GustoriaNavigationActions
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.viewmodel.SettingsViewModel
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)

            val isDarkTheme = when (settingsViewModel.selectedTheme) {
                0 -> false
                1 -> true
                else -> isSystemInDarkTheme()
            }

            val appFontScale = 0.85f + (settingsViewModel.fontSize * 0.45f)

            // Grab the phone's default screen density
            val currentDensity = LocalDensity.current

            GustoriaTheme(darkTheme = isDarkTheme) {

                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = currentDensity.density,
                        fontScale = appFontScale
                    )
                ) {
                    val navController = rememberNavController()
                    val navActions = remember(navController) {
                        GustoriaNavigationActions(navController)
                    }
                    GustoriaApp(navController, navActions)
                }

            }
        }
    }
}
