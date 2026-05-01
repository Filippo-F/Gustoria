package com.example.gustoria.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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

@Preview(name = "Small Phone", showSystemUi = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Standard Phone", showSystemUi = true, device = Devices.PHONE)
@Preview(name = "Big Tall Phone", showSystemUi = true, device = "spec:width=412dp,height=915dp,dpi=420")
@Preview(name = "Long Scroll View", showBackground = true, heightDp = 1500)
@Preview(name = "Tablet 4:3", showSystemUi = true, device = Devices.TABLET)
@Preview(name = "Foldable Inner", showSystemUi = true, device = Devices.FOLDABLE)
@Preview(name = "Landscape", showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
fun EditRecipeScreenPreview() {
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
        EditRecipeScreen(
            recipeRepository = fakeRepo,
            recipeId = "preview_id",
            onSaved = {},
            onCancel = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    recipeRepository: RecipeRepoInterface,
    recipeId: String?,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    vm: EditRecipeViewModel = viewModel(
        key = recipeId ?: "create_mode", // If recipeId is null, we're in create mode
        factory = EditRecipeViewModel.factory(recipeRepository, recipeId)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isEditMode = vm.isEditMode

    // If we are uploading info onto DB, show a loading indicator
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ScrollState to allow the user to scroll down the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditMode) "Edit Recipe" else "Create New Recipe",
            style = MaterialTheme.typography.headlineMedium
        )

        // Name
        OutlinedTextField(
            value = state.name,
            onValueChange = vm::updateName,
            label = { Text("Recipe Name *") },
            isError = state.errors.containsKey("name"),
            supportingText = { state.errors["name"]?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Description
        OutlinedTextField(
            value = state.description,
            onValueChange = vm::updateDescription,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Cost and difficulty
        Text("Cost", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ALL_COSTS.forEach { c ->
                FilterChip(
                    selected = state.cost == c,
                    onClick = { vm.updateCost(c) },
                    label = { Text(c) }
                )
            }
        }

        Text("Difficulty", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ALL_DIFFICULTIES.forEach { d ->
                FilterChip(
                    selected = state.difficulty == d,
                    onClick = { vm.updateDifficulty(d) },
                    label = { Text(d) }
                )
            }
        }

        // Time and servings
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.cookingTimeMinutesText,
                onValueChange = vm::updateCookingTime,
                label = { Text("Time (min) *") },
                isError = state.errors.containsKey("cookingTime"),
                supportingText = { state.errors["cookingTime"]?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = state.servingsText,
                onValueChange = vm::updateServings,
                label = { Text("Servings *") },
                isError = state.errors.containsKey("servings"),
                supportingText = { state.errors["servings"]?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Ingredients (dynamic list):
        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
        state.errors["ingredients"]?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

        state.ingredients.forEachIndexed { index, ingredient ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = ingredient.name,
                    onValueChange = { vm.updateIngredientName(index, it) },
                    label = { Text("Name") },
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = if (ingredient.quantity == 0) "" else ingredient.quantity.toString(),
                    onValueChange = { vm.updateIngredientQuantity(index, it) },
                    label = { Text("Qty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = ingredient.unit,
                    onValueChange = { vm.updateIngredientUnit(index, it) },
                    label = { Text("Unit") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { vm.removeIngredient(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Ingredient", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        TextButton(onClick = vm::addIngredient) { Text("+ Add Ingredient") }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Steps/Instructions (dynamic list):
        Text("Preparation Steps", style = MaterialTheme.typography.titleMedium)
        state.errors["steps"]?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

        state.steps.forEachIndexed { index, step ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = step,
                    onValueChange = { vm.updateStep(index, it) },
                    label = { Text("Step ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { vm.removeStep(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Step", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        TextButton(onClick = vm::addStep) { Text("+ Add Step") }

        Spacer(Modifier.height(16.dp))

        // Save and cancel buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = { vm.saveRecipe(onSuccess = onSaved) }) {
                Text("Save Recipe")
            }
        }
        Spacer(Modifier.height(32.dp)) // Extra padding for bottom scrolling
    }
}