package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
fun OwnedProfileScreen(viewModel: OwnedProfileViewModel, onBack: () -> Unit) {
    val user by viewModel.user.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("My Profile", style = MaterialTheme.typography.headlineLarge)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        ProfileDetail(label = "Full Name", value = user.fullName)
        ProfileDetail(label = "Username", value = user.username)
        ProfileDetail(label = "Email", value = user.email)
        ProfileDetail(label = "Location", value = user.location ?: "N/A")
        ProfileDetail(label = "Bio", value = user.bio)
        
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back")
        }
    }
}

@Composable
fun OtherProfileScreen(viewModel: OtherProfileViewModel, onBack: () -> Unit) {
    val user by viewModel.user.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("User Profile", style = MaterialTheme.typography.headlineLarge)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        ProfileDetail(label = "Name", value = user.fullName)
        ProfileDetail(label = "Username", value = user.username)
        ProfileDetail(label = "Location", value = user.location ?: "N/A")
        ProfileDetail(label = "Bio", value = user.bio)
        
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back")
        }
    }
}

@Composable
fun RecipeScreen(viewModel: RecipeViewModel, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Recipe View", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Text("Mockup recipe content will go here.", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
fun ProfileDetail(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BasicButton(modifier: Modifier = Modifier, displayText: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(0.8f)
    ) {
        Text(displayText, style = MaterialTheme.typography.bodyLarge)
    }
}
