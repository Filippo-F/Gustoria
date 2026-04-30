package com.example.gustoria.ui.recipe

import androidx.lifecycle.ViewModel
import com.example.gustoria.Dataclass.RecipeIngredient
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class EditRecipeUiState(
    val name: String = "",
    val description: String = "",
    val cost: String = ALL_COSTS.first(),
    val difficulty: String = ALL_DIFFICULTIES.first(),
    val cookingTimeMinutesText: String = "",
    val servingsText: String = "1",
    val imageUri: String = "",
    val ingredients: List<RecipeIngredient> = listOf(RecipeIngredient()),
    val steps: List<String> = listOf(""),
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = true
)

private const val ERR_NAME = "name"
private const val ERR_DESCRIPTION = "description"
private const val ERR_TIME = "cookingTime"
private const val ERR_SERVINGS = "servings"
private const val ERR_INGREDIENTS = "ingredients"
private const val ERR_STEPS = "steps"

class EditRecipeViewModel(
    private val recipeRepository: RecipeRepoInterface,
    private val recipeId: String? // null => create mode
) : ViewModel() {

    val isEditMode: Boolean get() = recipeId != null

    private val _state = MutableStateFlow(EditRecipeUiState(isLoading = isEditMode))
    val state: StateFlow<EditRecipeUiState> = _state.asStateFlow()
}