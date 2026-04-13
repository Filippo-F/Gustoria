package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.theme.GustoriaTheme

enum class Screen {
    MAIN, OWNED_PROFILE, OTHER_PROFILE, RECIPE
}

private var myProfile = UserProfile(
    fullName = "Name Surname",
    nickname = "SuperChef",
    email = "chef@gustoria.it",
    description = "---"
)
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

@Preview
@Composable
fun OwnedProfileScreenPreview () {
    OwnedProfileScreen(
        viewModel = OwnedProfileViewModel(),
        onBack = {}
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OwnedProfileScreen(viewModel: OwnedProfileViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
        ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            maxItemsInEachRow = 3,
        ) {
            // Back Icon
            IconButton(
                modifier = Modifier.width(56.dp),
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            // Page name
            Box (
                modifier = Modifier.height(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Profile Page", fontSize = 20.sp)
            }

            // Profile mini-picture
            Box (
                modifier = Modifier.height(56.dp).width(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("P", fontSize = 20.sp)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.guest_user_profile_pic),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop, // crops to fill the circle
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, Color.Gray, CircleShape) // width, color, shape
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun OtherProfileScreen(viewModel: OtherProfileViewModel, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Other User Profile Page", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
fun RecipeScreen(viewModel: RecipeViewModel, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Recipe View Page", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
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
