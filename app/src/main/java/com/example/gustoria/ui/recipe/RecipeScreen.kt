package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.Edit
import com.example.gustoria.Home
import com.example.gustoria.Search
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.viewmodel.RecipeViewModel
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.ui.utils.PreviewUtils

@MultiPreview
@Composable
fun RecipeScreenPreview() {
    val fakeRepo = PreviewUtils.createFakeRecipeRepo()

    GustoriaTheme(dynamicColor = false) {
        RecipeScreen(
            navCtrl = rememberNavController(),
            recipeRepository = fakeRepo
        )
    }
}

class RecipeScreenActions(val navCtrl : NavHostController) {
    val goHome: () -> Unit = {
        navCtrl.navigate(Home)
    }

    val navigateBack: () -> Unit = {
        navCtrl.popBackStack()
    }

    val onEditRecipe: (String) -> Unit = { id ->
        navCtrl.navigate(Edit(id))
    }
    val onAdjustFilters: () -> Unit = {
        navCtrl.navigate(Search.Searching) { launchSingleTop = true }
    }
}

@Composable
fun RecipeScreen(
    navCtrl: NavHostController,
    recipeRepository: RecipeRepoInterface,
    initialRecipeId: String? = null,
    viewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModel.provideFactory(recipeRepository)
    )
) {
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val selectedRecipe by viewModel.selectedRecipe.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    val actions = remember(navCtrl) {
        RecipeScreenActions(navCtrl)
    }

    LaunchedEffect(initialRecipeId) {
        if (initialRecipeId != null) {
            viewModel.selectRecipe(initialRecipeId)
        }
    }

    BackHandler(enabled = selectedRecipe != null) {
        viewModel.selectRecipe(null)
    }

    if (selectedRecipe == null) {
        RecipeListContent(
            recipes = filteredRecipes,
            filters = filters,
            onBack = actions.navigateBack,
            onAdjustFilters = actions.onAdjustFilters,
            onRecipeClick = viewModel::selectRecipe,
            onRemoveFilter = { filterLabel ->
                if (filterLabel in ALL_COSTS) viewModel.toggleCost(filterLabel)
                else if (filterLabel in ALL_DIFFICULTIES) viewModel.toggleDifficulty(filterLabel)
                else if (filterLabel == filters.nameQuery) viewModel.updateNameQuery("")
                else if (filterLabel == filters.ingredientQuery) viewModel.updateIngredientQuery("")
            }
        )
    } else {
        RecipeDetailsScreen(
            navCtrl = navCtrl,
            recipe = selectedRecipe!!,
            isOwner = viewModel.isOwnedByCurrentUser(selectedRecipe!!),
            onBackClick = { viewModel.selectRecipe(null) },
            onDeleteClick = { viewModel.deleteRecipe(selectedRecipe!!.id) },
            onDuplicateClick = {
                viewModel.duplicateRecipe(selectedRecipe!!) { newId ->
                    actions.onEditRecipe(newId)
                    viewModel.selectRecipe(null)
                }
            },
            onEditClick = { actions.onEditRecipe(selectedRecipe!!.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListContent(
    recipes: List<Recipe>,
    filters: RecipeFilters,
    onBack: () -> Unit,
    onAdjustFilters: () -> Unit,
    onRecipeClick: (String) -> Unit,
    onRemoveFilter: (String) -> Unit
) {
    // Gather all active filters into a single list
    val activeFilters = mutableListOf<String>()
    if (filters.nameQuery.isNotBlank()) activeFilters.add(filters.nameQuery)
    if (filters.ingredientQuery.isNotBlank()) activeFilters.add(filters.ingredientQuery)
    activeFilters.addAll(filters.selectedCosts)
    activeFilters.addAll(filters.selectedDifficulties)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        ThreeItemTopNavbar(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            title = "Search Results",
            onBack = onBack,
            showBackButton = true,
            extraIcon = Icons.Default.Search,
            extraIconDescription = "Adjust Filters",
            onClickExtra = onAdjustFilters
        )

        if (activeFilters.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(activeFilters) { filterLabel ->
                    InputChip(
                        selected = true,
                        onClick = { onRemoveFilter(filterLabel) },
                        label = { Text(filterLabel, fontSize = 12.sp) },
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = InputChipDefaults.inputChipBorder(
                            enabled = true,
                            selected = true,
                            borderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (recipes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(top = 32.dp), contentAlignment = Alignment.TopCenter) {
                Text(
                    "No recipes match your filters.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
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