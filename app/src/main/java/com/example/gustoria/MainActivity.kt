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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.theme.GustoriaTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.statusBarsPadding

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
    var currentScreen by rememberSaveable { mutableStateOf(Screen.MAIN) }

    when (currentScreen) {
        Screen.MAIN -> MainScreen(onNavigate = { currentScreen = it })
        Screen.OWNED_PROFILE -> OwnedProfileScreen(
            viewModel = ownedProfileViewModel,
            onBack = { currentScreen = Screen.MAIN },
            onNavigate = { currentScreen = Screen.MAIN }
        )
        Screen.OTHER_PROFILE -> OtherProfileScreen(
            viewModel = otherProfileViewModel,
            onBack = { currentScreen = Screen.MAIN },
            onNavigate = { currentScreen = Screen.MAIN }
        )
        Screen.RECIPE -> RecipeScreen(
            viewModel = recipeViewModel,
            onBack = { currentScreen = Screen.MAIN },
            onNavigate = { currentScreen = Screen.MAIN }
        )
    }
}

@Composable
fun MainScreen(onNavigate: (Screen) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Titolo
        Text(
            text = "GUSTORIA",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 6.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Your culinary world",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 64.dp, top = 8.dp)
        )

        // bottoni
        BasicButton(
            displayText = "Owned Profile",
            icon = Icons.Default.Person,
            onClick = { onNavigate(Screen.OWNED_PROFILE) }
        )
        //BasicButton(displayText = "OWNED User Profile", onClick = { onNavigate(Screen.OWNED_PROFILE) })
        Spacer(Modifier.height(20.dp))
        BasicButton(displayText = "OTHER User Profile", onClick = { onNavigate(Screen.OTHER_PROFILE) })
        Spacer(Modifier.height(20.dp))
        BasicButton(displayText = "Recipe View", onClick = { onNavigate(Screen.RECIPE) })
    }
}

@Composable
fun BasicButton(displayText: String, modifier: Modifier = Modifier, icon: ImageVector? = null, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(displayText, style = MaterialTheme.typography.labelLarge)
    }
}
