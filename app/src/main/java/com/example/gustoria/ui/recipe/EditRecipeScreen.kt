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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import androidx.navigation.NavHostController

class EditRecipeActions(private val navController: NavHostController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}

@MultiPreview
@Composable
fun EditRecipeScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        EditRecipeScreen(
            state = com.example.gustoria.viewmodel.EditRecipeUiState(),
            isEditMode = false,
            onBack = {},
            onDelete = {},
            onRevert = {},
            onSave = {},
            onUpdateImageUri = {},
            onUpdateName = {},
            onUpdateDescription = {},
            onUpdateCost = {},
            onUpdateDifficulty = {},
            onUpdateCookingTime = {},
            onUpdateServings = {},
            onUpdateIngredientName = { _, _ -> },
            onUpdateIngredientQuantity = { _, _ -> },
            onUpdateIngredientUnit = { _, _ -> },
            onRemoveIngredient = {},
            onAddIngredient = {},
            onUpdateStep = { _, _ -> },
            onRemoveStep = {},
            onAddStep = {}
        )
    }
}

@Composable
fun EditRecipeScreen(
    state: com.example.gustoria.viewmodel.EditRecipeUiState,
    isEditMode: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onRevert: () -> Unit,
    onSave: () -> Unit,
    onUpdateImageUri: (String) -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateCost: (String) -> Unit,
    onUpdateDifficulty: (String) -> Unit,
    onUpdateCookingTime: (String) -> Unit,
    onUpdateServings: (String) -> Unit,
    onUpdateIngredientName: (Int, String) -> Unit,
    onUpdateIngredientQuantity: (Int, String) -> Unit,
    onUpdateIngredientUnit: (Int, String) -> Unit,
    onRemoveIngredient: (Int) -> Unit,
    onAddIngredient: () -> Unit,
    onUpdateStep: (Int, String) -> Unit,
    onRemoveStep: (Int) -> Unit,
    onAddStep: () -> Unit
) {
    BackHandler {
        onRevert()
        onBack()
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
                        onRevert()
                        onBack()
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
                                    onDelete()
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
                                text = { Text("Clear All") },
                                onClick = {
                                    showTopMenu = false
                                    onRevert()
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
                onValueChange = onUpdateImageUri,
                label = { Text("Image URL or asset path") },
                placeholder = { Text("file:///android_asset/pasta.jpg") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                )

            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = onUpdateName,
                label = { Text("Recipe Name *") },
                isError = state.errors.containsKey("name"),
                supportingText = { state.errors["name"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
                )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = onUpdateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
                )

            // Cost
            Text("Cost", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ALL_COSTS.forEach { c ->
                    FilterChip(
                        selected = state.cost == c,
                        onClick = { onUpdateCost(c) },
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
                        onClick = { onUpdateDifficulty(d) },
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
                    onValueChange = onUpdateCookingTime,
                    label = { Text("Time (min) *") },
                    isError = state.errors.containsKey("cookingTime"),
                    supportingText = { state.errors["cookingTime"]?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                    )

                OutlinedTextField(
                    value = state.servingsText,
                    onValueChange = onUpdateServings,
                    label = { Text("Servings *") },
                    isError = state.errors.containsKey("servings") && state.servingsText.isBlank(),
                    supportingText = { state.errors["servings"]?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
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
                        onValueChange = { onUpdateIngredientName(index, it) },
                        label = { Text("Name") },
                        isError = ingredient.name.isBlank() && state.errors.containsKey("ingredients"),
                        modifier = Modifier.weight(2f)
                        )
                    OutlinedTextField(
                        value = if (ingredient.quantity == 0) "" else ingredient.quantity.toString(),
                        onValueChange = { onUpdateIngredientQuantity(index, it) },
                        label = { Text("Qty") },
                        isError = ingredient.quantity <= 0 && state.errors.containsKey("ingredients"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                        )
                    OutlinedTextField(
                        value = ingredient.unit,
                        onValueChange = { onUpdateIngredientUnit(index, it) },
                        label = { Text("Unit") },
                        isError = ingredient.unit.isBlank() && state.errors.containsKey("ingredients"),
                        modifier = Modifier.weight(1f)
                        )
                    IconButton(onClick = { onRemoveIngredient(index) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove Ingredient",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            TextButton(
                onClick = onAddIngredient,
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
                        onValueChange = { onUpdateStep(index, it) },
                        isError = step.isBlank() && state.errors.containsKey("steps"),
                        placeholder = { Text("Describe this step...") },
                        modifier = Modifier.weight(1f)
                        )
                    IconButton(onClick = { onRemoveStep(index) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove Step",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            TextButton(
                onClick = onAddStep,
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
                    onRevert()
                    onBack()
                }) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(16.dp))
                Button(onClick = onSave) {
                    Text("Save Recipe")
                }
            }

            Spacer(Modifier.height(32.dp)) // Extra padding for bottom scrolling
        }
    }
}
