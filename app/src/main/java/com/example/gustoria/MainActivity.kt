package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gustoria.model.PaperRecipeRepo
import com.example.gustoria.ui.recipe.RecipeScreen
import com.example.gustoria.ui.theme.GustoriaTheme
import io.paperdb.Paper

class MainActivity : ComponentActivity() {
    private lateinit var recipeRepository: PaperRecipeRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(applicationContext)

        setContent {
            GustoriaTheme {
                GustoriaApp(
                    recipeRepository = recipeRepository
                )
            }
        }
    }
}

enum class MainScreen {
    HOME,
    RECIPES_LIST,
    MY_RECIPES,
    CREATE_RECIPE
}

@Composable
fun GustoriaApp(
    recipeRepository: PaperRecipeRepo
) {
    var currentScreen by remember {
        mutableStateOf(MainScreen.HOME)
    }

    when (currentScreen) {
        MainScreen.HOME -> {
            MainMenuScreen(
                onRecipesListClick = {
                    currentScreen = MainScreen.RECIPES_LIST
                },
                onMyRecipesClick = {
                    currentScreen = MainScreen.MY_RECIPES
                },
                onCreateRecipeClick = {
                    currentScreen = MainScreen.CREATE_RECIPE
                }
            )
        }

        MainScreen.RECIPES_LIST -> {
            RecipeScreen(
                recipeRepository = recipeRepository
            )
        }

        MainScreen.MY_RECIPES -> {
            Text("Create Recipe Screen - TODO")
        }

        MainScreen.CREATE_RECIPE -> {
            Text("Create Recipe Screen - TODO")
        }
    }
}

@Composable
fun MainMenuScreen(
    onRecipesListClick: () -> Unit,
    onMyRecipesClick: () -> Unit,
    onCreateRecipeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gustoria",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRecipesListClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Recipes List")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onMyRecipesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Recipe Proposals")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCreateRecipeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Recipe Proposal")
        }
    }
}