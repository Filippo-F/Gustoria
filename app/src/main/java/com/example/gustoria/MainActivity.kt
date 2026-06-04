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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()

            // (0: Light, 1: Dark, 2: Auto)
            val isDarkTheme = when (settingsViewModel.selectedTheme) {
                0 -> false
                1 -> true
                else -> isSystemInDarkTheme()
            }

            GustoriaTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navActions = remember(navController) {
                    GustoriaNavigationActions(navController)
                }
                GustoriaApp(navController, navActions)
            }
        }
    }
}
