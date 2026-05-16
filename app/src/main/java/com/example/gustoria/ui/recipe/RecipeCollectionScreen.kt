package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.ThreeItemTopNavbar
import androidx.navigation.NavHostController
import com.example.gustoria.Create
import com.example.gustoria.Edit
import com.example.gustoria.Favourite
import com.example.gustoria.RecipeDetails
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.recipe.components.RecipeFilterSection
import com.example.gustoria.viewmodel.RecipeCollectionViewModel
import com.example.gustoria.domain.UserRepoInterface

class RecipeCollectionActions(val navController: NavHostController) {
    val onCreateNewRecipe: () -> Unit = {
        navController.navigate(Create)
    }
    val onEditRecipe: (String) -> Unit = { id ->
        navController.navigate(Edit(id))
    }
    val onTabChange: (Int) -> Unit = { tabIndex ->
        when (tabIndex) {
            0 -> navController.navigate(Favourite.Saved) { launchSingleTop = true; restoreState = true }
            1 -> navController.navigate(Favourite.Tried) { launchSingleTop = true; restoreState = true }
            2 -> navController.navigate(Favourite.Created) { launchSingleTop = true; restoreState = true }
        }
    }
    val onRecipeClick: (String) -> Unit = { id ->
        navController.navigate(RecipeDetails(id))
    }
}

@Composable
fun RecipeCollectionScreen(
    recipeRepository: RecipeRepoInterface,
    userRepository: UserRepoInterface,
    initialTab: Int = 0,
    navController: NavHostController,
    vm: RecipeCollectionViewModel = viewModel(
        factory = RecipeCollectionViewModel.factory(recipeRepository, userRepository)
    )
) {
    val recipes by vm.recipesToShow.collectAsStateWithLifecycle()
    val filters by vm.filters.collectAsStateWithLifecycle()
    val currentTab by vm.currentTab.collectAsStateWithLifecycle()

    val actions = remember(navController) { RecipeCollectionActions(navController) }

    // Sync initial tab
    LaunchedEffect(initialTab) {
        vm.setTab(initialTab)
    }

    RecipeCollectionListContent(
        recipes = recipes,
        filters = filters,
        currentTab = currentTab,
        onTabSelected = {
            vm.setTab(it)
            actions.onTabChange(it)
        },
        onCreateNewRecipe = actions.onCreateNewRecipe,
        onEditRecipe = actions.onEditRecipe,
        onRecipeClick = { recipe -> actions.onRecipeClick(recipe.id) },
        onDeleteRecipe = vm::delete,
        onNameQueryChange = vm::setNameQuery,
        onIngredientQueryChange = vm::setIngredientQuery,
        onToggleCost = vm::toggleCost,
        onToggleDifficulty = vm::toggleDifficulty,
        onResetFilters = vm::resetFilters
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeCollectionListContent(
    recipes: List<Recipe>,
    filters: RecipeFilters,
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
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

    val tabs = listOf("Saved", "Tried", "Created")
    val isEditingAllowed = currentTab == 2 // Only editing/creating on "Created" tab

    Column(modifier = Modifier.fillMaxSize()) {
        ThreeItemTopNavbar(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            title = "My Recipes",
            showBackButton = false // Hidden
        )

        SecondaryTabRow(
            selectedTabIndex = currentTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary,
            divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Show "New Recipe" button only if on the Created tab
            if (isEditingAllowed) {
                Button(onClick = onCreateNewRecipe, modifier = Modifier.fillMaxWidth()) {
                    Text("New Recipe")
                }
                Spacer(Modifier.height(12.dp))
            }

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
                Text("No recipes found in this tab.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recipes, key = { it.id }) { r ->
                        RecipeCard(
                            recipe = r,
                            onClick = { onRecipeClick(r) }
                        ) {
                            com.example.gustoria.ui.recipe.components.RecipeCardContent(recipe = r)

                            // Only show Edit/Delete buttons on the Created tab
                            if (isEditingAllowed) {
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
