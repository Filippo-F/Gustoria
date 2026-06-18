package com.example.gustoria.ui.recipe

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
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringArrayResource
import com.example.gustoria.R
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.ui.recipe.components.RecipeCard

@Composable
fun RecipeCollectionScreen(
    recipes: List<Recipe>,
    filters: RecipeFilters,
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    onCreateNewRecipe: () -> Unit,
    onEditRecipe: (String) -> Unit,
    onRecipeClick: (String) -> Unit,
    onDeleteRecipe: (String) -> Unit,
    onNameQueryChange: (String) -> Unit,
    onIngredientQueryChange: (String) -> Unit,
    onToggleCost: (String) -> Unit,
    onToggleDifficulty: (String) -> Unit,
    onOpenFilters: () -> Unit
) {
    RecipeCollectionListContent(
        recipes = recipes,
        filters = filters,
        currentTab = currentTab,
        onTabSelected = onTabSelected,
        onCreateNewRecipe = onCreateNewRecipe,
        onEditRecipe = onEditRecipe,
        onRecipeClick = onRecipeClick,
        onDeleteRecipe = onDeleteRecipe,
        onNameQueryChange = onNameQueryChange,
        onIngredientQueryChange = onIngredientQueryChange,
        onToggleCost = onToggleCost,
        onToggleDifficulty = onToggleDifficulty,
        onOpenFilters = onOpenFilters
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
    onRecipeClick: (String) -> Unit,
    onDeleteRecipe: (String) -> Unit,
    onNameQueryChange: (String) -> Unit,
    onIngredientQueryChange: (String) -> Unit,
    onToggleCost: (String) -> Unit,
    onToggleDifficulty: (String) -> Unit,
    onOpenFilters: () -> Unit
) {
    val allCosts = stringArrayResource(R.array.recipe_costs).toList()
    val allDifficulties = stringArrayResource(R.array.recipe_difficulties).toList()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var idToDelete by remember { mutableStateOf("") }
    var nameToDelete by remember { mutableStateOf("") }

    val tabs = listOf("Saved", "Tried", "Created")
    val isCreatedTab = currentTab == 2

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
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    AssistChip(
                        onClick = onOpenFilters,
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
                                style = MaterialTheme.typography.labelMedium
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

                items(activeFilters) { label ->
                    InputChip(
                        selected = true,
                        onClick = {
                            when (label) {
                                in allCosts -> onToggleCost(label)
                                in allDifficulties -> onToggleDifficulty(label)
                                filters.nameQuery -> onNameQueryChange("")
                                filters.ingredientQuery -> onIngredientQueryChange("")
                            }
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
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
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete recipe") },
                text = { Text("Delete \"$nameToDelete\"?") },
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