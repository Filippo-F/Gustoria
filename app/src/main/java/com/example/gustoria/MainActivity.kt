package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.model.PaperRecipeRepo
import com.example.gustoria.ui.recipe.EditRecipeScreen
import com.example.gustoria.ui.recipe.OwnedRecipeScreen
import com.example.gustoria.ui.recipe.RecipeScreen
import com.example.gustoria.ui.theme.GustoriaTheme
import io.paperdb.Paper

class MainActivity : ComponentActivity() {
    private lateinit var recipeRepository: RecipeRepoInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(applicationContext)
        recipeRepository = PaperRecipeRepo()

        setContent {
            GustoriaTheme {
                GustoriaApp(recipeRepository = recipeRepository)
            }
        }
    }
}

enum class MainScreen {
    HOME,
    RECIPES_LIST,
    MY_RECIPES,
    CREATE_RECIPE,
    EDIT_RECIPE
}

@Composable
fun GustoriaApp(recipeRepository: RecipeRepoInterface) {

    var currentScreen by rememberSaveable { mutableStateOf(MainScreen.HOME) }

    var editingRecipeId by rememberSaveable { mutableStateOf<String?>(null) }

    BackHandler(enabled = currentScreen != MainScreen.HOME) {
        currentScreen = MainScreen.HOME
        editingRecipeId = null
    }

    when (currentScreen) {
        MainScreen.HOME -> {
            MainMenuScreen(
                onRecipesListClick = { currentScreen = MainScreen.RECIPES_LIST },
                onMyRecipesClick = { currentScreen = MainScreen.MY_RECIPES },
                onCreateRecipeClick = {
                    editingRecipeId = null
                    currentScreen = MainScreen.CREATE_RECIPE
                }
            )
        }

        MainScreen.RECIPES_LIST -> {
            RecipeScreen(
                recipeRepository = recipeRepository,
                onEditRecipe = { id ->
                    editingRecipeId = id
                    currentScreen = MainScreen.EDIT_RECIPE
                },
                onBack = { currentScreen = MainScreen.HOME }
            )
        }

        MainScreen.MY_RECIPES -> {
            OwnedRecipeScreen(
                recipeRepository = recipeRepository,
                onCreateNewRecipe = {
                    editingRecipeId = null
                    currentScreen = MainScreen.CREATE_RECIPE
                },
                onEditRecipe = { id ->
                    editingRecipeId = id
                    currentScreen = MainScreen.EDIT_RECIPE
                },
                onBack = { currentScreen = MainScreen.HOME }
            )
        }

        MainScreen.CREATE_RECIPE -> {
            EditRecipeScreen(
                recipeRepository = recipeRepository,
                recipeId = null,
                onSaved = { currentScreen = MainScreen.MY_RECIPES },
                onCancel = { currentScreen = MainScreen.HOME }
            )
        }

        MainScreen.EDIT_RECIPE -> {
            EditRecipeScreen(
                recipeRepository = recipeRepository,
                recipeId = editingRecipeId,
                onSaved = {
                    editingRecipeId = null
                    currentScreen = MainScreen.MY_RECIPES
                },
                onCancel = {
                    editingRecipeId = null
                    currentScreen = MainScreen.MY_RECIPES
                }
            )
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
            text = "GUSTORIA",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onRecipesListClick, modifier = Modifier.fillMaxWidth()) {
            Text("Recipes List")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onMyRecipesClick, modifier = Modifier.fillMaxWidth()) {
            Text("My Recipes")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onCreateRecipeClick, modifier = Modifier.fillMaxWidth()) {
            Text("Create New Recipe")
        }
    }
}
