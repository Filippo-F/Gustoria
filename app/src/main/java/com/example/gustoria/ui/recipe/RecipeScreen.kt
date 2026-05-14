package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.viewmodel.RecipeViewModel
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.recipe.components.RecipeFilterSection
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.ui.utils.PreviewUtils

@MultiPreview
@Composable
fun RecipeScreenPreview() {
    val fakeRepo = PreviewUtils.createFakeRecipeRepo()

    GustoriaTheme(dynamicColor = false) {
        RecipeScreen(
            recipeRepository = fakeRepo,
            onEditRecipe = {},
            onBack = {}
        )
    }
}

@Composable
fun RecipeScreen(
    recipeRepository: RecipeRepoInterface,
    onEditRecipe: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModel.provideFactory(recipeRepository)
    )
) {
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val selectedRecipe by viewModel.selectedRecipe.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    BackHandler(enabled = selectedRecipe != null) {
        viewModel.selectRecipe(null)
    }

    if (selectedRecipe == null) {
        RecipeListContent(
            recipes = filteredRecipes,
            filters = filters,
            onBack = onBack,
            onRecipeClick = viewModel::selectRecipe,
            onNameQueryChange = viewModel::updateNameQuery,
            onIngredientQueryChange = viewModel::updateIngredientQuery,
            onToggleCost = viewModel::toggleCost,
            onToggleDifficulty = viewModel::toggleDifficulty,
            onClearFilters = viewModel::resetFilters
        )
    } else {
        RecipeDetailsScreen(
            recipe = selectedRecipe!!,
            isOwner = viewModel.isOwnedByCurrentUser(selectedRecipe!!),
            onBackClick = { viewModel.selectRecipe(null) },
            onDeleteClick = { viewModel.deleteRecipe(selectedRecipe!!.id) },
            onDuplicateClick = {
                viewModel.duplicateRecipe(selectedRecipe!!) { newId ->
                    onEditRecipe(newId)
                    viewModel.selectRecipe(null)
                }
            },
            onEditClick = { onEditRecipe(selectedRecipe!!.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListContent(
    recipes: List<Recipe>,
    filters: RecipeFilters,
    onBack: () -> Unit,
    onRecipeClick: (String) -> Unit,
    onNameQueryChange: (String) -> Unit,
    onIngredientQueryChange: (String) -> Unit,
    onToggleCost: (String) -> Unit,
    onToggleDifficulty: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        ThreeItemTopNavbar(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            title = "Recipes",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {

            RecipeFilterSection(
                nameQuery = filters.nameQuery,
                onNameQueryChange = onNameQueryChange,
                ingredientQuery = filters.ingredientQuery,
                onIngredientQueryChange = onIngredientQueryChange,
                selectedCosts = filters.selectedCosts,
                onToggleCost = onToggleCost,
                selectedDifficulties = filters.selectedDifficulties,
                onToggleDifficulty = onToggleDifficulty,
                onResetFilters = onClearFilters,
                showResetButton = !filters.isEmpty
            )

            Spacer(Modifier.height(8.dp))

            if (recipes.isEmpty()) {
                Text(
                    "No recipes match the current filters.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}
