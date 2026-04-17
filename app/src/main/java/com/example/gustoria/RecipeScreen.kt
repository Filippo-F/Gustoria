package com.example.gustoria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import com.example.gustoria.ui.AppBottomNavBar
import com.example.gustoria.ui.NavDestination

@Preview(name = "Portrait", showSystemUi = true)
@Composable
fun RecipeScreenPreviewPortrait() {
    MaterialTheme {
        RecipeScreen(viewModel = viewModel(), onBack = {}, onNavigate = {})
    }
}

@Composable
fun RecipeScreen(viewModel: RecipeViewModel, onBack: () -> Unit = {}, onNavigate: (NavDestination) -> Unit) {
    Scaffold(
        bottomBar = {
            AppBottomNavBar(
                currentDestination = NavDestination.EXPLORE,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Recipe View Page", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack) {
            Text("Back")
        }
    }
}}
