package com.example.gustoria.ui.recipe

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.utils.MultiPreview

@MultiPreview
@Composable
fun RecipeScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        RecipeScreen(
            recipes = emptyList(),
            filters = RecipeFilters(),
            onBack = {},
            onAdjustFilters = {},
            onRecipeClick = {},
            onRemoveFilter = {}
        )
    }
}

@Composable
fun RecipeScreen(
    recipes: List<Recipe>,
    filters: RecipeFilters,
    onBack: () -> Unit,
    onAdjustFilters: () -> Unit,
    onRecipeClick: (String) -> Unit,
    onRemoveFilter: (String) -> Unit
) {
    RecipeListContent(
        recipes = recipes,
        filters = filters,
        onBack = onBack,
        onAdjustFilters = onAdjustFilters,
        onRecipeClick = onRecipeClick,
        onRemoveFilter = onRemoveFilter
    )
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
    activeFilters.addAll(filters.selectedCuisines)
    activeFilters.addAll(filters.selectedMealTypes)
    activeFilters.addAll(filters.selectedDietaryTags)
    filters.maxCookingTimeMinutes?.let { activeFilters.add("≤${it}min") }

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
                        label = { Text(filterLabel, style = MaterialTheme.typography.labelMedium) },
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