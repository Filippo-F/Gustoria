package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.theme.GustoriaTheme

enum class Screen {
    MAIN, OWNED_PROFILE, OTHER_PROFILE, RECIPE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GustoriaTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(
    ownedProfileViewModel: OwnedProfileViewModel = viewModel(),
    otherProfileViewModel: OtherProfileViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentScreen) {
                Screen.MAIN -> MainScreen(onNavigate = { currentScreen = it })
                Screen.OWNED_PROFILE -> OwnedProfileScreen(ownedProfileViewModel) { currentScreen = Screen.MAIN }
                Screen.OTHER_PROFILE -> OtherProfileScreen(otherProfileViewModel) { currentScreen = Screen.MAIN }
                Screen.RECIPE -> RecipeScreen(recipeViewModel) { currentScreen = Screen.MAIN }
            }
        }
    }
}

@Composable
fun MainScreen(onNavigate: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BasicButton(displayText = "OWNED User Profile", onClick = { onNavigate(Screen.OWNED_PROFILE) })
        Spacer(Modifier.height(20.dp))
        BasicButton(displayText = "OTHER User Profile", onClick = { onNavigate(Screen.OTHER_PROFILE) })
        Spacer(Modifier.height(20.dp))
        BasicButton(displayText = "Recipe View", onClick = { onNavigate(Screen.RECIPE) })
    }
}

@Composable
fun BasicButton(displayText: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(displayText, style = MaterialTheme.typography.labelLarge)
    }
}
