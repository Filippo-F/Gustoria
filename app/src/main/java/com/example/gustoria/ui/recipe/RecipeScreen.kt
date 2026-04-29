package com.example.gustoria.ui.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface

@Composable
fun RecipeScreen(
    recipeRepository: RecipeRepoInterface,
    viewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModel.provideFactory(recipeRepository)
    )
) {
    val recipes = viewModel.recipes.collectAsStateWithLifecycle().value
    val selectedRecipe = viewModel.selectedRecipe.collectAsStateWithLifecycle().value

    RecipeContent(
        recipes = recipes,
        selectedRecipe = selectedRecipe,
        onRecipeClick = viewModel::selectRecipe,
        onBackClick = { viewModel.selectRecipe(null) },
        onDeleteClick = viewModel::deleteRecipe,
        onDuplicateClick = viewModel::duplicateRecipe
    )
}

@Composable
fun RecipeContent(
    recipes: List<Recipe>,
    selectedRecipe: Recipe?,
    onRecipeClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onDuplicateClick: (Recipe) -> Unit
) {
    if (selectedRecipe == null) {
        RecipeListContent(
            recipes = recipes,
            onRecipeClick = onRecipeClick
        )
    } else {
        RecipeDetailsContent(
            recipe = selectedRecipe,
            onBackClick = onBackClick,
            onDeleteClick = { onDeleteClick(selectedRecipe.id) },
            onDuplicateClick = { onDuplicateClick(selectedRecipe) }
        )
    }
}

@Composable
fun RecipeListContent(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(recipes) { recipe ->
            Card(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable { onRecipeClick(recipe.id) } // faccio solo partire l'evento
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${recipe.cost} • ${recipe.difficulty} • ${recipe.cookingTimeMinutes} min"
                    )

                    Text(
                        text = "Servings: ${recipe.servings}"
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeDetailsContent(
    recipe: Recipe,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDuplicateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = onBackClick) {
            Text("Back")
        }

        Text(
            text = recipe.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(recipe.description)

        Text("Cost: ${recipe.cost}")
        Text("Difficulty: ${recipe.difficulty}")
        Text("Cooking time: ${recipe.cookingTimeMinutes} min")
        Text("Servings: ${recipe.servings}")
        Text("Rating: ${recipe.rating}")

        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        recipe.ingredients.forEach { ingredient ->
            Text("- ${ingredient.name} (${ingredient.kcalPer100g} kcal/100g)")
        }

        Text(
            text = "Steps",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        recipe.steps.forEachIndexed { index, step ->
            Text("${index + 1}. $step")
        }

        Button(
            onClick = onDuplicateClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Duplicate recipe")
        }

        Button(
            onClick = onDeleteClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Delete recipe")
        }
    }
}