package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.recipe.EditRecipeScreen
import com.example.gustoria.ui.recipe.RecipeDetailsScreen
import com.example.gustoria.viewmodel.EditRecipeViewModel
import kotlinx.serialization.Serializable

@Serializable
object Create

@Serializable
data class Edit(val recipeId: String)

@Serializable
data class RecipeDetails(val recipeId: String)

@Composable
fun CreateRecipeDestination(
    navActions: GustoriaNavigationActions,
    viewModel: EditRecipeViewModel = viewModel(
        key = "create_mode",
        factory = EditRecipeViewModel.factory(null)
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditRecipeScreen(
        state = state,
        isEditMode = false,
        onBack = navActions::navigateBack,
        onDelete = {}, // Not applicable for create
        onRevert = viewModel::revertChanges,
        onSave = { viewModel.saveRecipe(onSuccess = navActions::navigateBack) },
        onUpdateImageUri = viewModel::updateImageUri,
        onUpdateName = viewModel::updateName,
        onUpdateDescription = viewModel::updateDescription,
        onUpdateCost = viewModel::updateCost,
        onUpdateDifficulty = viewModel::updateDifficulty,
        onUpdateCuisineType = viewModel::updateCuisineType,
        onUpdateMealType = viewModel::updateMealType,
        onToggleDietaryTag = viewModel::toggleDietaryTag,
        onUpdateCookingTime = viewModel::updateCookingTime,
        onUpdateServings = viewModel::updateServings,
        onUpdateIngredientName = viewModel::updateIngredientName,
        onUpdateIngredientQuantity = viewModel::updateIngredientQuantity,
        onUpdateIngredientUnit = viewModel::updateIngredientUnit,
        onRemoveIngredient = viewModel::removeIngredient,
        onAddIngredient = viewModel::addIngredient,
        onUpdateStep = viewModel::updateStep,
        onRemoveStep = viewModel::removeStep,
        onAddStep = viewModel::addStep
    )
}

@Composable
fun EditRecipeDestination(
    recipeId: String,
    navActions: GustoriaNavigationActions,
    viewModel: EditRecipeViewModel = viewModel(
        key = recipeId,
        factory = EditRecipeViewModel.factory(recipeId)
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditRecipeScreen(
        state = state,
        isEditMode = true,
        onBack = navActions::navigateBack,
        onDelete = { viewModel.deleteRecipe(onSuccess = navActions::navigateBack) },
        onRevert = viewModel::revertChanges,
        onSave = { viewModel.saveRecipe(onSuccess = navActions::navigateBack) },
        onUpdateImageUri = viewModel::updateImageUri,
        onUpdateName = viewModel::updateName,
        onUpdateDescription = viewModel::updateDescription,
        onUpdateCost = viewModel::updateCost,
        onUpdateDifficulty = viewModel::updateDifficulty,
        onUpdateCuisineType = viewModel::updateCuisineType,
        onUpdateMealType = viewModel::updateMealType,
        onToggleDietaryTag = viewModel::toggleDietaryTag,
        onUpdateCookingTime = viewModel::updateCookingTime,
        onUpdateServings = viewModel::updateServings,
        onUpdateIngredientName = viewModel::updateIngredientName,
        onUpdateIngredientQuantity = viewModel::updateIngredientQuantity,
        onUpdateIngredientUnit = viewModel::updateIngredientUnit,
        onRemoveIngredient = viewModel::removeIngredient,
        onAddIngredient = viewModel::addIngredient,
        onUpdateStep = viewModel::updateStep,
        onRemoveStep = viewModel::removeStep,
        onAddStep = viewModel::addStep
    )
}

@Composable
fun RecipeDetailsDestination(
    recipeId: String,
    navActions: GustoriaNavigationActions
) {
    RecipeDetailsScreen(
        recipeId = recipeId,
        navActions = navActions
    )
}
