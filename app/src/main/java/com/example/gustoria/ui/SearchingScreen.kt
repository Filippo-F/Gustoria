package com.example.gustoria.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gustoria.ui.recipe.ALL_COSTS
import com.example.gustoria.ui.recipe.ALL_DIFFICULTIES
import com.example.gustoria.viewmodel.RecipeViewModel

@Composable
fun SearchingScreen(
    vm: RecipeViewModel,
    onClose: () -> Unit,
    onShowResultsClick: () -> Unit
) {
    val filters by vm.filters.collectAsStateWithLifecycle()
    val filteredRecipes by vm.filteredRecipes.collectAsStateWithLifecycle()

    // Mock UI data for things not yet in ViewModel (Time, Cuisine, specific ingredients)
    val timeOptions = listOf("Any", "< 15m", "< 30m", "< 60m")
    val ingredientOptions = listOf("Tomato", "Garlic", "Chicken", "Olive Oil")
    val cuisineOptions = listOf("Italian", "Mexican", "Japanese", "Indian")

    var selectedTime by remember { mutableStateOf("Any") }
    var selectedCuisine by remember { mutableStateOf("Italian") }
    var selectedIngredients by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier.fillMaxWidth().height(56.dp)
                    .background(MaterialTheme.colorScheme.background),
                title = "Filters",
                onBack = onClose,
                showBackButton = true,
                extraIcon = Icons.Default.Refresh,
                extraIconDescription = "Reset Filters",
                onClickExtra = {
                    vm.resetFilters()
                    selectedTime = "Any"
                    selectedIngredients = emptySet()
                }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Show ${filteredRecipes.size} Results", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

            // 1. DIFFICULTY
            FilterSectionTitle("Difficulty")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ALL_DIFFICULTIES.forEach { diff ->
                    FilterChip(
                        selected = diff in filters.selectedDifficulties,
                        onClick = { vm.toggleDifficulty(diff) },
                        label = { Text(diff) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. TIME (Mocked for UI)
            FilterSectionTitle("Time")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                timeOptions.forEach { time ->
                    FilterChip(
                        selected = selectedTime == time,
                        onClick = { selectedTime = time },
                        label = { Text(time) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. COST
            FilterSectionTitle("Cost")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ALL_COSTS.forEach { cost ->
                    FilterChip(
                        selected = cost in filters.selectedCosts,
                        onClick = { vm.toggleCost(cost) },
                        label = { Text(cost) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. INGREDIENTS
            FilterSectionTitle("Specific Ingredients")
            ingredientOptions.forEach { ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = filters.ingredientQuery == ingredient,
                            role = Role.RadioButton,       // radio??
                            onValueChange = { isChecked ->
                                if (isChecked) vm.updateIngredientQuery(ingredient)
                                else vm.updateIngredientQuery("")
                            }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = ingredient in selectedIngredients, onCheckedChange = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = ingredient, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. CUISINE TYPE
            FilterSectionTitle("Cuisine Type")
            cuisineOptions.forEach { cuisine ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = selectedCuisine == cuisine,
                            role = Role.RadioButton,
                            onValueChange = { selectedCuisine = cuisine }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedCuisine == cuisine, onClick = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = cuisine, fontSize = 16.sp)
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
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}