package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.viewmodel.OwnedRecipeViewModel
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.recipe.components.RecipeFilterSection
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.ui.utils.PreviewUtils

@MultiPreview
@Composable
fun OwnedRecipeScreenPreview() {
    val fakeRepo = PreviewUtils.createFakeRecipeRepo()

    GustoriaTheme(dynamicColor = false) {
        OwnedRecipeScreen(
            recipeRepository = fakeRepo,
            onCreateNewRecipe = {},
            onEditRecipe = {},
            onBack = {}
        )
    }
}

@Composable
fun OwnedRecipeScreen(
    recipeRepository: RecipeRepoInterface,
    onCreateNewRecipe: () -> Unit,
    onEditRecipe: (String) -> Unit,
    onBack: () -> Unit,
    vm: OwnedRecipeViewModel = viewModel(
        factory = OwnedRecipeViewModel.factory(recipeRepository)
    )
) {
    val recipes by vm.recipesToShow.collectAsStateWithLifecycle()
    val filters by vm.filters.collectAsStateWithLifecycle()
    val selectedRecipe by vm.selectedRecipe.collectAsStateWithLifecycle()

    BackHandler(enabled = selectedRecipe != null) {
        vm.selectRecipe(null)
    }

    if (selectedRecipe == null) {
        OwnedRecipeListContent(
            recipes = recipes,
            filters = filters,
            onBack = onBack,
            onCreateNewRecipe = onCreateNewRecipe,
            onEditRecipe = onEditRecipe,
            onRecipeClick = vm::selectRecipe,
            onDeleteRecipe = vm::delete,
            onNameQueryChange = vm::setNameQuery,
            onIngredientQueryChange = vm::setIngredientQuery,
            onToggleCost = vm::toggleCost,
            onToggleDifficulty = vm::toggleDifficulty,
            onResetFilters = vm::resetFilters
        )
    } else {
        RecipeDetailsScreen(
            recipe = selectedRecipe!!,
            isOwner = true,
            onBackClick = { vm.selectRecipe(null) },
            onDeleteClick = { vm.delete(selectedRecipe!!.id) },
            onDuplicateClick = {
                vm.duplicateRecipe(selectedRecipe!!) { newId ->
                    onEditRecipe(newId)
                    vm.selectRecipe(null)
                }
            },
            onEditClick = {
                onEditRecipe(selectedRecipe!!.id)
                vm.selectRecipe(null)
            }
        )
    }
}

@Composable
private fun OwnedRecipeListContent(
    recipes: List<Recipe>,
    filters: RecipeFilters,
    onBack: () -> Unit,
    onCreateNewRecipe: () -> Unit,
    onEditRecipe: (String) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onDeleteRecipe: (String) -> Unit,
    onNameQueryChange: (String) -> Unit,
    onIngredientQueryChange: (String) -> Unit,
    onToggleCost: (String) -> Unit,
    onToggleDifficulty: (String) -> Unit,
    onResetFilters: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var idToDelete by remember { mutableStateOf("") }
    var nameToDelete by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        ThreeItemTopNavbar(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            title = "My Recipes",
            onBack = onBack
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Button(onClick = onCreateNewRecipe, modifier = Modifier.fillMaxWidth()) {
                Text("New Recipe")
            }

            Spacer(Modifier.height(12.dp))

            RecipeFilterSection(
                nameQuery = filters.nameQuery,
                onNameQueryChange = onNameQueryChange,
                ingredientQuery = filters.ingredientQuery,
                onIngredientQueryChange = onIngredientQueryChange,
                selectedCosts = filters.selectedCosts,
                onToggleCost = onToggleCost,
                selectedDifficulties = filters.selectedDifficulties,
                onToggleDifficulty = onToggleDifficulty,
                onResetFilters = onResetFilters,
                showResetButton = !filters.isEmpty
            )

            Spacer(Modifier.height(8.dp))

            if (recipes.isEmpty()) {
                Text("No recipes found")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recipes) { r ->
                        RecipeCard(
                            recipe = r,
                            onClick = { onRecipeClick(r) }
                        ) {
                            com.example.gustoria.ui.recipe.components.RecipeCardContent(recipe = r)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onEditRecipe(r.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit")
                                }
                                Button(
                                    onClick = {
                                        idToDelete = r.id
                                        nameToDelete = r.name
                                        showDeleteDialog = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete recipe", color = Color.Black) },
                text = { Text("Delete \"$nameToDelete\"?", color = Color.Black) },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteRecipe(idToDelete)
                        showDeleteDialog = false
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
