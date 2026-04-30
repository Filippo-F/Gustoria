package com.example.gustoria.ui.recipe

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.domain.RecipeRepoInterface

@Composable
fun OwnedRecipeScreen(
    recipeRepository: RecipeRepoInterface,
    onCreateNewRecipe: () -> Unit,
    onEditRecipe: (String) -> Unit,
    vm: OwnedRecipeViewModel = viewModel(
        factory = OwnedRecipeViewModel.factory(recipeRepository)
    )
) {
    val recipes by vm.recipesToShow.collectAsStateWithLifecycle()
    val name by vm.nameQuery.collectAsStateWithLifecycle()
    val ingredient by vm.ingredientQuery.collectAsStateWithLifecycle()
    val costs by vm.costs.collectAsStateWithLifecycle()
    val difficulties by vm.difficulties.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var idToDelete by remember { mutableStateOf("") }
    var nameToDelete by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Recipes", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(8.dp))

        Button(onClick = onCreateNewRecipe, modifier = Modifier.fillMaxWidth()) {
            Text("New Recipe")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = vm::setNameQuery,
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ingredient,
            onValueChange = vm::setIngredientQuery,
            label = { Text("Search by ingredient") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text("Cost", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("€", "€€", "€€€").forEach { c ->
                FilterChip(
                    selected = c in costs,
                    onClick = { vm.toggleCost(c) },
                    label = { Text(c) }
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text("Difficulty", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Easy", "Medium", "Hard").forEach { d ->
                FilterChip(
                    selected = d in difficulties,
                    onClick = { vm.toggleDifficulty(d) },
                    label = { Text(d) }
                )
            }
        }

        TextButton(onClick = vm::resetFilters) {
            Text("Reset filters")
        }

        Spacer(Modifier.height(8.dp))

        if (recipes.isEmpty()) {
            Text("No recipes found")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recipes) { r ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditRecipe(r.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(r.name, style = MaterialTheme.typography.titleMedium)
                            Text("${r.cost} - ${r.difficulty} - ${r.cookingTimeMinutes} min")
                            Text("Servings: ${r.servings}")

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
            title = { Text("Delete recipe") },
            text = { Text("Delete \"$nameToDelete\"?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.delete(idToDelete)
                    showDeleteDialog = false
                }) {
                    Text("Delete")
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
