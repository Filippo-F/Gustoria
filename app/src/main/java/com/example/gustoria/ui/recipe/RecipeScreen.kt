package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import com.example.gustoria.ui.theme.GustoriaTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.viewmodel.RecipeViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.gustoria.R

@Preview(name = "Small Phone", showSystemUi = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Standard Phone", showSystemUi = true, device = Devices.PHONE)
@Preview(name = "Big Tall Phone", showSystemUi = true, device = "spec:width=412dp,height=915dp,dpi=420")
@Preview(name = "Long Scroll View", showBackground = true, heightDp = 1500)
@Preview(name = "Tablet 4:3", showSystemUi = true, device = Devices.TABLET)
@Preview(name = "Foldable Inner", showSystemUi = true, device = Devices.FOLDABLE)
@Preview(name = "Landscape", showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")

@Composable
fun RecipeScreenPreview() {
    val fakeRepo = object : RecipeRepoInterface {
        override fun getAllRecipes(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getRecipeById(recipeId: String): Flow<Recipe?> = flowOf(
            Recipe(
                name = "Pasta al Pomodoro",
                description = "A classic Italian pasta dish with fresh tomatoes and basil.",
                cost = "€",
                difficulty = "Easy",
                cookingTimeMinutes = 15,
                servings = 2,
                steps = listOf("Boil water", "Cook pasta", "Prepare sauce", "Mix and serve")
            )
        )
        override fun getRecipeByOwner(ownerId: String): Flow<List<Recipe>> = flowOf(emptyList())
        override suspend fun addRecipe(recipe: Recipe) {}
        override suspend fun updateRecipe(recipeId: String, recipe: Recipe) {}
        override suspend fun deleteRecipe(recipeId: String) {}
    }

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
            onCostRangeChange = viewModel::updateCostRange,
            onToggleDifficulty = viewModel::toggleDifficulty,
            onClearFilters = viewModel::clearFilters
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
    onCostRangeChange: (Int, Int) -> Unit,
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
                .fillMaxSize()
                .padding(16.dp)
        ) {

            /*Text(
            text = "Recipes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )*/

            // 1. Search by name
            OutlinedTextField(
                value = filters.nameQuery,
                onValueChange = onNameQueryChange,
                label = { Text("Search by name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // 2. by ingredient
            OutlinedTextField(
                value = filters.ingredientQuery,
                onValueChange = onIngredientQueryChange,
                label = { Text("Filter by ingredient") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // 3. Cost filter
            Text("Cost", style = MaterialTheme.typography.labelMedium)
            var selectedCosts by remember { mutableStateOf(emptySet<Int>()) }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val costOptions = listOf("€" to 1, "€€" to 2, "€€€" to 3)

                costOptions.forEach { (label, value) ->
                    val isSelected = selectedCosts.contains(value)

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newSelection = if (isSelected) {
                                selectedCosts - value
                            } else {
                                selectedCosts + value
                            }
                            selectedCosts = newSelection

                            if (newSelection.isEmpty()) {
                                onCostRangeChange(1, 3)
                            } else {
                                val min = newSelection.minOrNull() ?: 1
                                val max = newSelection.maxOrNull() ?: 3
                                onCostRangeChange(min, max)
                            }
                        },
                        label = { Text(label) }
                    )
                }
            }

            // 4. Difficulty filter
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
                TextButton(onClick = onClearFilters) {
                    Text("Clear filters")
                }
            }

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
                        RecipeGridCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun RecipeGridCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = recipe.imageUri,
                contentDescription = "Image of ${recipe.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                // If link is null or broken, use "no_image"
                fallback = painterResource(id = R.drawable.no_image),
                error = painterResource(id = R.drawable.no_image)
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = recipe.name.ifBlank { "Untitled recipe" },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${recipe.cost} • ${recipe.difficulty}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "${recipe.cookingTimeMinutes} min · ${recipe.servings} serv.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

