package com.example.gustoria.ui.recipe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringArrayResource
import com.example.gustoria.R

@Composable
fun RecipeFilterSection(
    nameQuery: String,
    onNameQueryChange: (String) -> Unit,
    ingredientQuery: String,
    onIngredientQueryChange: (String) -> Unit,
    selectedCosts: Set<String>,
    onToggleCost: (String) -> Unit,
    selectedDifficulties: Set<String>,
    onToggleDifficulty: (String) -> Unit,
    onResetFilters: () -> Unit,
    showResetButton: Boolean = true
) {
    val allCosts = stringArrayResource(R.array.recipe_costs).toList()
    val allDifficulties = stringArrayResource(R.array.recipe_difficulties).toList()

    Column {
        OutlinedTextField(
            value = nameQuery,
            onValueChange = onNameQueryChange,
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ingredientQuery,
            onValueChange = onIngredientQueryChange,
            label = { Text("Search by ingredient") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        Text("Cost", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allCosts.forEach { c ->
                FilterChip(
                    selected = c in selectedCosts,
                    onClick = { onToggleCost(c) },
                    label = { Text(c) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text("Difficulty", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allDifficulties.forEach { d ->
                FilterChip(
                    selected = d in selectedDifficulties,
                    onClick = { onToggleDifficulty(d) },
                    label = { Text(d) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
            }
        }

        if (showResetButton) {
            TextButton(onClick = onResetFilters) {
                Text("Reset filters")
            }
        }
    }
}
