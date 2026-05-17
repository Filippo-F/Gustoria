package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.recipe.components.RecipeFilterSection
import com.example.gustoria.viewmodel.RecipeCollectionViewModel
import com.example.gustoria.domain.UserRepoInterface
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.ui.Alignment

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
    val selectedRecipe by vm.selectedRecipe.collectAsStateWithLifecycle()
    val currentTab by vm.currentTab.collectAsStateWithLifecycle()

    val actions = remember(navController) { RecipeCollectionActions(navController) }

    // Sync initial tab
    LaunchedEffect(initialTab) {
        vm.setTab(initialTab)
    }

    BackHandler(enabled = selectedRecipe != null) {
        vm.selectRecipe(null)
    }

    if (selectedRecipe == null) {
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
            onRecipeClick = vm::selectRecipe,
            onDeleteRecipe = vm::delete,
            onNameQueryChange = vm::setNameQuery,
            onIngredientQueryChange = vm::setIngredientQuery,
            onToggleCost = vm::toggleCost,
            onToggleDifficulty = vm::toggleDifficulty,
            onResetFilters = vm::resetFilters
        )
    } else {
        //controlla se la ricetta è nei favourites come in RecipeScreen
        val isFav by vm
            .let { collectionVm ->
                // RecipeCollectionViewModel non ha isFavouriteFlow nominato così, lo costruisco
                collectionVm.isFavouriteFlow(selectedRecipe!!.id)
            }
            .collectAsStateWithLifecycle(initialValue = false)

        RecipeDetailsScreen(
            navCtrl = navController,
            recipe = selectedRecipe!!,
            isOwner = currentTab == 2, //solo Owner
            isFavourite = isFav,
            onToggleFavourite = { vm.toggleFavourite(selectedRecipe!!.id) },
            onBackClick = { vm.selectRecipe(null) },
            onDeleteClick = { vm.delete(selectedRecipe!!.id) },
            onDuplicateClick = {
                vm.duplicateRecipe(selectedRecipe!!) { newId ->
                    actions.onEditRecipe(newId)
                    vm.selectRecipe(null)
                }
            },
            onEditClick = {
                actions.onEditRecipe(selectedRecipe!!.id)
                vm.selectRecipe(null)
            }
        )
    }
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
    var showFiltersSheet by remember { mutableStateOf(false) }

    val tabs = listOf("Saved", "Tried", "Created")
    val isCreatedTab = currentTab == 2

    //filtri attivi in chip
    val activeFilters = remember(filters) {
        buildList {
            if (filters.nameQuery.isNotBlank()) add(filters.nameQuery)
            if (filters.ingredientQuery.isNotBlank()) add(filters.ingredientQuery)
            addAll(filters.selectedCosts)
            addAll(filters.selectedDifficulties)
        }
    }

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                title = "My Recipes",
                showBackButton = false
            )
        },
        floatingActionButton = {
            if (isCreatedTab) {
                FloatingActionButton(
                    onClick = onCreateNewRecipe,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Recipe")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
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

            // Riga sempre visibile: chip "Filters" + chip filtri attivi
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //"Filters" sempre visibile, con etichetta
                item {
                    AssistChip(
                        onClick = { showFiltersSheet = true },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (activeFilters.isEmpty()) "Filters"
                                else "Filters (${activeFilters.size})",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null
                    )
                }

                //Active filter chips (rimovibili) accanto
                items(activeFilters) { label ->
                    InputChip(
                        selected = true,
                        onClick = {
                            when (label) {
                                in ALL_COSTS -> onToggleCost(label)
                                in ALL_DIFFICULTIES -> onToggleDifficulty(label)
                                filters.nameQuery -> onNameQueryChange("")
                                filters.ingredientQuery -> onIngredientQueryChange("")
                            }
                        },
                        label = { Text(label, fontSize = 12.sp) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            //lista a 2 colonne (griglia)
            if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = when (currentTab) {
                            0 -> "No saved recipes yet."
                            1 -> "No recipes marked as tried."
                            else -> "You haven't created any recipes."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    gridItems(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe) }
                        )
                    }
                }
            }
        }

        // filtri completi
        if (showFiltersSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFiltersSheet = false },
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Filters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
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
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { showFiltersSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Done") }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        // Delete dialog (per quando elimini dal dettaglio, lo lasciamo)
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
