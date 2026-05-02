package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.theme.GustoriaTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.viewmodel.OwnedRecipeViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.example.gustoria.R

@Preview(name = "Small Phone", showSystemUi = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Standard Phone", showSystemUi = true, device = Devices.PHONE)
@Preview(name = "Big Tall Phone", showSystemUi = true, device = "spec:width=412dp,height=915dp,dpi=420")
@Preview(name = "Long Scroll View", showBackground = true, heightDp = 1500)
@Preview(name = "Tablet 4:3", showSystemUi = true, device = Devices.TABLET)
@Preview(name = "Foldable Inner", showSystemUi = true, device = Devices.FOLDABLE)
@Preview(name = "Landscape", showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")

@Composable
fun OwnedRecipeScreenPreview() {
    val fakeRepo = object : RecipeRepoInterface {
        override fun getAllRecipes(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getRecipeById(recipeId: String): Flow<Recipe?> = flowOf(
            Recipe(
                name = "Pasta al Pomodoro",
                description = "A classic Italian pasta dish with fresh tomatoes and basil.",
                cost = "€",
                difficulty = "Low",
                cookingTimeMinutes = 15,
                servings = 2,
                steps = listOf("Boil water", "Cook pasta", "Prepare sauce", "Mix and serve")
            )
        )
        override fun getRecipeByOwner(ownerId: String): Flow<List<Recipe>> = flowOf(emptyList())
        override suspend fun addRecipe(recipe: Recipe) {}
        override suspend fun updateRecipe(recipeId: String, recipe: Recipe) {}
        override suspend fun deleteRecipe(recipeId: String) {}
    }

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
    val name by vm.nameQuery.collectAsStateWithLifecycle()
    val ingredient by vm.ingredientQuery.collectAsStateWithLifecycle()
    val costs by vm.costs.collectAsStateWithLifecycle()
    val difficulties by vm.difficulties.collectAsStateWithLifecycle()
    val selectedRecipe by vm.selectedRecipe.collectAsStateWithLifecycle()

    BackHandler(enabled = selectedRecipe != null) {
        vm.selectRecipe(null)
    }

    if (selectedRecipe != null) {
        RecipeDetailsScreen(
            recipe = selectedRecipe!!,
            isOwner = true,
            onBackClick = { vm.selectRecipe(null) },
            onDeleteClick = { vm.delete(selectedRecipe!!.id) },
            onDuplicateClick = { vm.duplicateRecipe(selectedRecipe!!) },
            onEditClick = {
                onEditRecipe(selectedRecipe!!.id)
                vm.selectRecipe(null)
            }
        )
        return
    }

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
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                                .clickable { vm.selectRecipe(r) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                AsyncImage(
                                    model = r.imageUri,
                                    contentDescription = "Image of ${r.name}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                    // If link is null or broken, use "no_image"
                                    fallback = painterResource(id = R.drawable.no_image),
                                    error = painterResource(id = R.drawable.no_image)
                                )
                                Spacer(Modifier.height(8.dp))

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
}
