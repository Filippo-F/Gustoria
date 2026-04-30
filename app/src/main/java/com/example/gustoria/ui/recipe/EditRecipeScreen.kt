package com.example.gustoria.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.Dataclass.RecipeIngredient
import com.example.gustoria.domain.RecipeRepoInterface

@Composable
fun EditRecipeScreen(
    recipeRepository: RecipeRepoInterface,
    recipeId: String?,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: EditRecipeViewModel = viewModel(
        key = "EditRecipeViewModel-${recipeId ?: "new"}",
        factory = EditRecipeViewModel.provideFactory(recipeRepository, recipeId)
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    EditRecipeForm(
        state = state,
        isEditMode = viewModel.isEditMode,
        onNameChange = viewModel::updateName,
        onDescriptionChange = viewModel::updateDescription,
        onCostChange = viewModel::updateCost,
        onDifficultyChange = viewModel::updateDifficulty,
        onCookingTimeChange = viewModel::updateCookingTime,
        onServingsChange = viewModel::updateServings,
        onImageUriChange = viewModel::updateImageUri,
        onAddIngredient = viewModel::addIngredient,
        onRemoveIngredient = viewModel::removeIngredient,
        onIngredientNameChange = viewModel::updateIngredientName,
        onIngredientQuantityChange = viewModel::updateIngredientQuantity,
        onIngredientUnitChange = viewModel::updateIngredientUnit,
        onAddStep = viewModel::addStep,
        onRemoveStep = viewModel::removeStep,
        onStepChange = viewModel::updateStep,
        onSave = { viewModel.save(onSuccess = onSaved) },
        onCancel = onCancel
    )
}

@Composable
private fun EditRecipeForm(
    state: EditRecipeUiState,
    isEditMode: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onCookingTimeChange: (String) -> Unit,
    onServingsChange: (String) -> Unit,
    onImageUriChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (Int) -> Unit,
    onIngredientNameChange: (Int, String) -> Unit,
    onIngredientQuantityChange: (Int, String) -> Unit,
    onIngredientUnitChange: (Int, String) -> Unit,
    onAddStep: () -> Unit,
    onRemoveStep: (Int) -> Unit,
    onStepChange: (Int, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (isEditMode) "Edit recipe" else "New recipe",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            isError = "name" in state.errors,
            supportingText = { state.errors["name"]?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            isError = "description" in state.errors,
            supportingText = { state.errors["description"]?.let { Text(it) } },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text("Cost", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ALL_COSTS.forEach { cost ->
                FilterChip(
                    selected = cost == state.cost,
                    onClick = { onCostChange(cost) },
                    label = { Text(cost) }
                )
            }
        }

        // ----- Difficulty --------------------------------------------------
        Text("Difficulty", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ALL_DIFFICULTIES.forEach { diff ->
                FilterChip(
                    selected = diff == state.difficulty,
                    onClick = { onDifficultyChange(diff) },
                    label = { Text(diff) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.cookingTimeMinutesText,
                onValueChange = onCookingTimeChange,
                label = { Text("Cooking time (min)") },
                isError = "cookingTime" in state.errors,
                supportingText = { state.errors["cookingTime"]?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.servingsText,
                onValueChange = onServingsChange,
                label = { Text("Servings") },
                isError = "servings" in state.errors,
                supportingText = { state.errors["servings"]?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))


        OutlinedTextField(
            value = state.imageUri,
            onValueChange = onImageUriChange,
            label = { Text("") },
            placeholder = { Text("") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
        state.errors["ingredients"]?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        state.ingredients.forEachIndexed { index, ingredient ->
            IngredientRow(
                index = index,
                ingredient = ingredient,
                onNameChange = { onIngredientNameChange(index, it) },
                onQuantityChange = { onIngredientQuantityChange(index, it) },
                onUnitChange = { onIngredientUnitChange(index, it) },
                onRemove = { onRemoveIngredient(index) },
                canRemove = state.ingredients.size > 1
            )
        }

        TextButton(onClick = onAddIngredient) {
            Text("+ Add ingredient")
        }

        Spacer(Modifier.height(16.dp))


        Text("Steps", style = MaterialTheme.typography.titleMedium)
        state.errors["steps"]?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        state.steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = step,
                    onValueChange = { onStepChange(index, it) },
                    label = { Text("Step ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                if (state.steps.size > 1) {
                    TextButton(onClick = { onRemoveStep(index) }) {
                        Text("Remove")
                    }
                }
            }
        }

        TextButton(onClick = onAddStep) {
            Text("+ Add step")
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                Text(if (isEditMode) "Save changes" else "Create recipe")
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun IngredientRow(
    index: Int,
    ingredient: RecipeIngredient,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("Ingredient ${index + 1}", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = ingredient.name,
                onValueChange = onNameChange,
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.weight(2f)
            )
            OutlinedTextField(
                value = if (ingredient.quantity == 0) "" else ingredient.quantity.toString(),
                onValueChange = onQuantityChange,
                label = { Text("Qty") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = ingredient.unit,
                onValueChange = onUnitChange,
                label = { Text("Unit") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        if (canRemove) {
            TextButton(onClick = onRemove) { Text("Remove") }
        }
    }
}
