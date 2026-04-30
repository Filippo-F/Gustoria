package com.example.gustoria.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.Dataclass.RecipeIngredient
import com.example.gustoria.SessionManager
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    /* When in edit mode, preserve immutable fields. */
    private var originalRecipe: Recipe? = null

    init {
        if (recipeId != null) {
            viewModelScope.launch {
                val recipe = recipeRepository.getRecipeById(recipeId).first()
                if (recipe != null) {
                    originalRecipe = recipe
                    _state.value = EditRecipeUiState(
                        name = recipe.name,
                        description = recipe.description,
                        cost = recipe.cost.ifBlank { ALL_COSTS.first() },
                        difficulty = recipe.difficulty.ifBlank { ALL_DIFFICULTIES.first() },
                        cookingTimeMinutesText = recipe.cookingTimeMinutes.toString(),
                        servingsText = recipe.servings.toString(),
                        imageUri = recipe.imageUri.orEmpty(),
                        ingredients = recipe.ingredients.ifEmpty { listOf(RecipeIngredient()) },
                        steps = recipe.steps.ifEmpty { listOf("") },
                        isLoading = false
                    )
                } else {

                    _state.update { it.copy(isLoading = false) }
                }
            }
        } else {
            _state.update { it.copy(isLoading = false) }
        }
    }


    fun updateName(value: String) =
        _state.update { it.copy(name = value, errors = it.errors - ERR_NAME) }

    fun updateDescription(value: String) =
        _state.update { it.copy(description = value, errors = it.errors - ERR_DESCRIPTION) }

    fun updateCost(value: String) = _state.update { it.copy(cost = value) }
    fun updateDifficulty(value: String) = _state.update { it.copy(difficulty = value) }

    fun updateCookingTime(value: String) =
        _state.update { it.copy(cookingTimeMinutesText = value, errors = it.errors - ERR_TIME) }

    fun updateServings(value: String) =
        _state.update { it.copy(servingsText = value, errors = it.errors - ERR_SERVINGS) }

    fun updateImageUri(value: String) = _state.update { it.copy(imageUri = value) }

    // Ingredients

    fun addIngredient() = _state.update {
        it.copy(ingredients = it.ingredients + RecipeIngredient())
    }

    fun removeIngredient(index: Int) = _state.update {
        if (index !in it.ingredients.indices) it
        else it.copy(ingredients = it.ingredients.toMutableList().also { l -> l.removeAt(index) })
    }

    fun updateIngredientName(index: Int, value: String) = _state.update {
        if (index !in it.ingredients.indices) it
        else it.copy(
            ingredients = it.ingredients.mapIndexed { i, ing ->
                if (i == index) ing.copy(name = value) else ing
            },
            errors = it.errors - ERR_INGREDIENTS
        )
    }

    fun updateIngredientQuantity(index: Int, value: String) = _state.update {
        if (index !in it.ingredients.indices) it
        else it.copy(
            ingredients = it.ingredients.mapIndexed { i, ing ->
                if (i == index) ing.copy(quantity = value.toIntOrNull() ?: 0) else ing
            }
        )
    }

    fun updateIngredientUnit(index: Int, value: String) = _state.update {
        if (index !in it.ingredients.indices) it
        else it.copy(
            ingredients = it.ingredients.mapIndexed { i, ing ->
                if (i == index) ing.copy(unit = value) else ing
            }
        )
    }


    fun addStep() = _state.update { it.copy(steps = it.steps + "") }

    fun removeStep(index: Int) = _state.update {
        if (index !in it.steps.indices) it
        else it.copy(steps = it.steps.toMutableList().also { l -> l.removeAt(index) })
    }

    fun updateStep(index: Int, value: String) = _state.update {
        if (index !in it.steps.indices) it
        else it.copy(
            steps = it.steps.mapIndexed { i, s -> if (i == index) value else s },
            errors = it.errors - ERR_STEPS
        )
    }


    private fun validate(state: EditRecipeUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (state.name.isBlank()) errors[ERR_NAME] = "Name is required."
        if (state.description.isBlank()) errors[ERR_DESCRIPTION] = "Description is required."

        val time = state.cookingTimeMinutesText.toIntOrNull()
        if (time == null || time <= 0) errors[ERR_TIME] = "Cooking time must be a positive number."

        val serv = state.servingsText.toIntOrNull()
        if (serv == null || serv <= 0) errors[ERR_SERVINGS] = "Servings must be a positive number."

        val validIngredients = state.ingredients.filter { it.name.isNotBlank() }
        if (validIngredients.isEmpty()) errors[ERR_INGREDIENTS] = "Add at least one ingredient."

        val validSteps = state.steps.filter { it.isNotBlank() }
        if (validSteps.isEmpty()) errors[ERR_STEPS] = "Add at least one step."

        return errors
    }

    private fun buildRecipe(state: EditRecipeUiState): Recipe {
        val base = originalRecipe ?: Recipe(
            ownerId = SessionManager.CURRENT_LOGGED_IN_USER_ID
        )
        return base.copy(
            name = state.name.trim(),
            description = state.description.trim(),
            cost = state.cost,
            difficulty = state.difficulty,
            cookingTimeMinutes = state.cookingTimeMinutesText.toIntOrNull() ?: 0,
            servings = state.servingsText.toIntOrNull() ?: 1,
            imageUri = state.imageUri.takeIf { it.isNotBlank() },
            ingredients = state.ingredients.filter { it.name.isNotBlank() },
            steps = state.steps.filter { it.isNotBlank() }
        )
    }

    //save
    fun save(onSuccess: () -> Unit) {
        val current = _state.value
        val errors = validate(current)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors) }
            return
        }
        val recipe = buildRecipe(current)
        viewModelScope.launch {
            if (isEditMode) {
                recipeRepository.updateRecipe(recipe.id, recipe)
                _state.update { it.copy(errors = emptyMap()) }
            } else {
                recipeRepository.addRecipe(recipe)
                // Reset the form so a subsequent "Create" starts blank again.
                _state.value = EditRecipeUiState(isLoading = false)
            }
            onSuccess()
        }
    }

    companion object {
        fun provideFactory(
            recipeRepository: RecipeRepoInterface,
            recipeId: String? = null
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    EditRecipeViewModel(recipeRepository, recipeId) as T
            }
    }
}
