package com.example.gustoria.ui.recipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import com.example.gustoria.ui.ThreeItemTopNavbar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.viewmodel.EditRecipeViewModel
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.ui.utils.PreviewUtils

@MultiPreview
@Composable
fun EditRecipeScreenPreview() {
    val fakeRepo = PreviewUtils.createFakeRecipeRepo()

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

    BackHandler {
        vm.revertChanges()
        onCancel()
    }

    var showTopMenu by remember { mutableStateOf(false) }

    // If we are uploading info onto DB, show a loading indicator
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                ThreeItemTopNavbar(
                    modifier = Modifier.fillMaxWidth(),
                    title = if (isEditMode) "Edit Recipe" else "Create New Recipe",
                    onBack = {
                        vm.revertChanges()
                        onCancel()
                    },
                    extraIcon = Icons.Default.MoreVert,
                    extraIconDescription = "More Options",
                    onClickExtra = { showTopMenu = true }
                )
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    DropdownMenu(
                        expanded = showTopMenu,
                        onDismissRequest = { showTopMenu = false }
                    ) {
                        if (isEditMode) {
                            DropdownMenuItem(
                                text = { Text("Delete Recipe", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showTopMenu = false
                                    vm.deleteRecipe(onSuccess = onCancel)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Clear Form") },
                                onClick = {
                                    showTopMenu = false
                                    vm.revertChanges()
                                }
                            )
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        // ScrollState to allow the user to scroll down the screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

        // Image Preview and URL field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (state.imageUri.isNotBlank()) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = "Recipe Image Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        "No image",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Image URL field
            OutlinedTextField(
                value = state.imageUri,
                onValueChange = vm::updateImageUri,
                label = { Text("Image URL or asset path") },
                placeholder = { Text("file:///android_asset/pasta.jpg") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = vm::updateName,
                label = { Text("Recipe Name *") },
                isError = state.errors.containsKey("name"),
                supportingText = { state.errors["name"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = vm::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Cost
            Text("Cost", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ALL_COSTS.forEach { c ->
                    FilterChip(
                        selected = state.cost == c,
                        onClick = { vm.updateCost(c) },
                        label = { Text(c) },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }

            // Difficulty
            Text("Difficulty", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ALL_DIFFICULTIES.forEach { d ->
                    FilterChip(
                        selected = state.difficulty == d,
                        onClick = { vm.updateDifficulty(d) },
                        label = { Text(d) },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }

            // Time and servings
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.cookingTimeMinutesText,
                    onValueChange = vm::updateCookingTime,
                    label = { Text("Time (min) *") },
                    isError = state.errors.containsKey("cookingTime"),
                    supportingText = { state.errors["cookingTime"]?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                OutlinedTextField(
                    value = state.servingsText,
                    onValueChange = vm::updateServings,
                    label = { Text("Servings *") },
                    isError = state.errors.containsKey("servings") && state.servingsText.isBlank(),
                    supportingText = { state.errors["servings"]?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Ingredients (dynamic list):
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            state.errors["ingredients"]?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

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
                        isError = ingredient.name.isBlank() && state.errors.containsKey("ingredients"),
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = if (ingredient.quantity == 0) "" else ingredient.quantity.toString(),
                        onValueChange = { vm.updateIngredientQuantity(index, it) },
                        label = { Text("Qty") },
                        isError = ingredient.quantity <= 0 && state.errors.containsKey("ingredients"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = ingredient.unit,
                        onValueChange = { vm.updateIngredientUnit(index, it) },
                        label = { Text("Unit") },
                        isError = ingredient.unit.isBlank() && state.errors.containsKey("ingredients"),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    IconButton(onClick = { vm.removeIngredient(index) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove Ingredient",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            TextButton(
                onClick = vm::addIngredient,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add Ingredient")
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Steps/Instructions (dynamic list):
            Text(
                text = "Preparation Steps",
                style = MaterialTheme.typography.titleMedium
            )
            state.errors["steps"]?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            state.steps.forEachIndexed { index, step ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (index + 1).toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    OutlinedTextField(
                        value = step,
                        onValueChange = { vm.updateStep(index, it) },
                        isError = step.isBlank() && state.errors.containsKey("steps"),
                        placeholder = { Text("Describe this step...") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    IconButton(onClick = { vm.removeStep(index) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove Step",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            TextButton(
                onClick = vm::addStep,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add Step")
            }

            Spacer(Modifier.height(16.dp))

            // Save and cancel buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    vm.revertChanges()
                    onCancel()
                }) {
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
}