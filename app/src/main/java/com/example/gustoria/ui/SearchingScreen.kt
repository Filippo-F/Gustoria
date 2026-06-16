package com.example.gustoria.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gustoria.R
import com.example.gustoria.ui.recipe.RecipeFilters

@Composable
fun SearchingScreen(
    filters: RecipeFilters,
    resultCount: Int,
    onClose: () -> Unit,
    onShowResultsClick: () -> Unit,
    onResetFilters: () -> Unit,
    onToggleDifficulty: (String) -> Unit,
    onToggleCost: (String) -> Unit,
    onToggleCuisine: (String) -> Unit,
    onToggleMealType: (String) -> Unit,
    onToggleDietaryTag: (String) -> Unit,
    onSetMaxCookingTime: (Int?) -> Unit,
    onNameQueryChange: (String) -> Unit,
    onIngredientQueryChange: (String) -> Unit,
) {
    val difficulties  = stringArrayResource(R.array.recipe_difficulties).toList()
    val costs         = stringArrayResource(R.array.recipe_costs).toList()
    val cuisineTypes  = stringArrayResource(R.array.recipe_cuisine_types).toList()
    val mealTypes     = stringArrayResource(R.array.recipe_meal_types).toList()
    val dietaryTags   = stringArrayResource(R.array.recipe_dietary_tags).toList()
    val timeLabels    = stringArrayResource(R.array.recipe_time_filter_labels).toList()
    val timeMinutes   = integerArrayResource(R.array.recipe_time_filter_minutes).toList()

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colorScheme.background),
                title = "Filters",
                onBack = onClose,
                showBackButton = true,
                extraIcon = Icons.Default.Refresh,
                extraIconDescription = "Reset Filters",
                onClickExtra = { onResetFilters() }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onShowResultsClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Show $resultCount Results", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // search by name & ingredient
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = filters.nameQuery,
                onValueChange = onNameQueryChange,
                label = { Text("Search by name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = filters.ingredientQuery,
                onValueChange = onIngredientQueryChange,
                label = { Text("Search by ingredient") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            //DIFFICULTY
            FilterSectionTitle("Difficulty")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                difficulties.forEach { diff ->
                    FilterChip(
                        selected = diff in filters.selectedDifficulties,
                        onClick = { onToggleDifficulty(diff) },
                        label = { Text(diff) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            //time
            FilterSectionTitle("Time")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                timeLabels.forEachIndexed { index, label ->
                    val minutes = timeMinutes.getOrNull(index) ?: -1
                    val isSelected = when {
                        minutes == -1 -> filters.maxCookingTimeMinutes == null
                        else -> filters.maxCookingTimeMinutes == minutes
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onSetMaxCookingTime(if (minutes == -1) null else minutes)
                        },
                        label = { Text(label) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // cost
            FilterSectionTitle("Cost")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                costs.forEach { cost ->
                    FilterChip(
                        selected = cost in filters.selectedCosts,
                        onClick = { onToggleCost(cost) },
                        label = { Text(cost) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // cusine type
            FilterSectionTitle("Cuisine Type")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cuisineTypes.forEach { cuisine ->
                    FilterChip(
                        selected = cuisine in filters.selectedCuisines,
                        onClick = { onToggleCuisine(cuisine) },
                        label = { Text(cuisine) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            //Meal Type
            FilterSectionTitle("Meal Type")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mealTypes.forEach { meal ->
                    FilterChip(
                        selected = meal in filters.selectedMealTypes,
                        onClick = { onToggleMealType(meal) },
                        label = { Text(meal) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            //Dietary
            FilterSectionTitle("Dietary")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dietaryTags.forEach { tag ->
                    FilterChip(
                        selected = tag in filters.selectedDietaryTags,
                        onClick = { onToggleDietaryTag(tag) },
                        label = { Text(tag) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}