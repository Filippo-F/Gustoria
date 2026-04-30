package com.example.gustoria.ui.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface

@Composable
fun OwnedRecipeScreen(
    recipeRepository: RecipeRepoInterface,
    onCreateNewRecipe: () -> Unit,
    onEditRecipe: (String) -> Unit,
    viewModel: OwnedRecipeViewModel = viewModel(
        factory = OwnedRecipeViewModel.provideFactory(recipeRepository)
    )
) {
    val recipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    var pendingDelete by rememberSaveable { mutableStateOf<String?>(null) }
    val pendingDeleteRecipe = recipes.firstOrNull { it.id == pendingDelete }

    OwnedRecipeListContent(
        recipes = recipes,
        filters = filters,
        onCreateNewRecipe = onCreateNewRecipe,
        onEditRecipe = onEditRecipe,
        onDeleteRecipe = { pendingDelete = it },
        onNameQueryChange = viewModel::updateNameQuery,
        onIngredientQueryChange = viewModel::updateIngredientQuery,
        onToggleCost = viewModel::toggleCost,
        onToggleDifficulty = viewModel::toggleDifficulty,
        onClearFilters = viewModel::clearFilters
    )

    if (pendingDelete != null) {
        DeleteConfirmationDialog(
            recipeName = pendingDeleteRecipe?.name.orEmpty(),
            onConfirm = {
                viewModel.deleteRecipe(pendingDelete!!)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun OwnedRecipeListContent(
    recipes: List<Recipe>,
    filters: RecipeFilters,
    onCreateNewRecipe: () -> Unit,
    onEditRecipe: (String) -> Unit,
    onDeleteRecipe: (String) -> Unit,
    onNameQueryChange: (String) -> Unit,
    onIngredientQueryChange: (String) -> Unit,
    onToggleCost: (String) -> Unit,
    onToggleDifficulty: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "My Recipes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = onCreateNewRecipe,
            modifier = Modifier.fillMaxWidth()
        ) { Text("+ Create new recipe") }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = filters.nameQuery,
            onValueChange = onNameQueryChange,
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = filters.ingredientQuery,
            onValueChange = onIngredientQueryChange,
            label = { Text("Filter by ingredient") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text("Cost", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ALL_COSTS.forEach { cost ->
                FilterChip(
                    selected = cost in filters.selectedCosts,
                    onClick = { onToggleCost(cost) },
                    label = { Text(cost) }
                )
            }
        }

        Text("Difficulty", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ALL_DIFFICULTIES.forEach { diff ->
                FilterChip(
                    selected = diff in filters.selectedDifficulties,
                    onClick = { onToggleDifficulty(diff) },
                    label = { Text(diff) }
                )
            }
        }

        if (!filters.isEmpty) {
            TextButton(onClick = onClearFilters) { Text("Clear filters") }
        }

        Spacer(Modifier.height(8.dp))

        if (recipes.isEmpty()) {
            Text(
                text = "You don't have any recipe matching the filters yet.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(recipes, key = { it.id }) { recipe ->
                    OwnedRecipeRow(
                        recipe = recipe,
                        onEditClick = { onEditRecipe(recipe.id) },
                        onDeleteClick = { onDeleteRecipe(recipe.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OwnedRecipeRow(
    recipe: Recipe,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = recipe.name.ifBlank { "Untitled recipe" },
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${recipe.cost} • ${recipe.difficulty} • ${recipe.cookingTimeMinutes} min",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Servings: ${recipe.servings}  ·  ${recipe.ingredients.size} ingredients",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) { Text("Edit") }

                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) { Text("Delete") }
            }
        }
    }
}
