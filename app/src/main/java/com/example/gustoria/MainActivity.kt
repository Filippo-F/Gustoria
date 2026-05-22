package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.ui.navigation.GustoriaApp
import com.example.gustoria.ui.navigation.GustoriaNavigationActions
import com.example.gustoria.ui.theme.GustoriaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GustoriaTheme {
                val navController = rememberNavController() // Create the NavController
                val navActions = remember(navController) {
                    GustoriaNavigationActions(navController)
                } // Create the Navigation Actions, passing the NavController
                GustoriaApp(navController, navActions)
            }
        }
    }
}
