package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.dataclass.RecipeIngredient
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.ui.recipe.ALL_COSTS
import com.example.gustoria.ui.recipe.ALL_DIFFICULTIES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

class EditRecipeViewModel(
    private val recipeRepository: RecipeRepoInterface,
    private val userRepo: UserRepoInterface,
    private val reviewRepository: ReviewRepoInterface,
    private val notificationRepo: NotificationRepoInterface,
    private val recipeId: String? // null => we are in create mode
) : ViewModel() {

    val isEditMode: Boolean get() = recipeId != null

    private val _state = MutableStateFlow(EditRecipeUiState(isLoading = true))  // We set "isLoading" to true by default to show the loading screen immediately when screen is opened
    val state: StateFlow<EditRecipeUiState> = _state.asStateFlow()

    // Save original recipe in case we are in edit mode to keep track of changes
    private var originalRecipe: Recipe? = null

    init {
        if (isEditMode && recipeId != null) {
            // Insert data (edit mode)
            viewModelScope.launch {
                recipeRepository.getRecipeById(recipeId).take(1).collect { recipe ->
                    if (recipe != null) {
                        originalRecipe = recipe
                        _state.update {
                            it.copy(
                                name = recipe.name,
                                description = recipe.description,
                                cost = recipe.cost,
                                difficulty = recipe.difficulty,
                                cookingTimeMinutesText = recipe.cookingTimeMinutes.toString(),
                                servingsText = recipe.servings.toString(),
                                imageUri = recipe.imageUri ?: "",
                                ingredients = if (recipe.ingredients.isEmpty()) listOf(RecipeIngredient()) else recipe.ingredients,
                                steps = if (recipe.steps.isEmpty()) listOf("") else recipe.steps,
                                isLoading = false
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        } else {
            // New recipe creation (create mode), let's just stop the loading indicator
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun revertChanges() {
        val recipe = originalRecipe
        if (recipe != null) {
            _state.update {
                it.copy(
                    name = recipe.name,
                    description = recipe.description,
                    cost = recipe.cost,
                    difficulty = recipe.difficulty,
                    cookingTimeMinutesText = recipe.cookingTimeMinutes.toString(),
                    servingsText = recipe.servings.toString(),
                    imageUri = recipe.imageUri ?: "",
                    ingredients = if (recipe.ingredients.isEmpty()) listOf(RecipeIngredient()) else recipe.ingredients,
                    steps = if (recipe.steps.isEmpty()) listOf("") else recipe.steps,
                    errors = emptyMap(),
                    isLoading = false
                )
            }
        } else {
            // Reset to default create state
            _state.update {
                EditRecipeUiState(
                    isLoading = false,
                    ingredients = listOf(RecipeIngredient()),
                    steps = listOf("")
                )
            }
        }
    }

    // Fields update
    fun updateName(name: String) = _state.update { it.copy(name = name) }
    fun updateDescription(desc: String) = _state.update { it.copy(description = desc) }
    fun updateCost(cost: String) = _state.update { it.copy(cost = cost) }
    fun updateDifficulty(diff: String) = _state.update { it.copy(difficulty = diff) }
    fun updateCookingTime(time: String) = _state.update { it.copy(cookingTimeMinutesText = time) }
    fun updateServings(servings: String) = _state.update { it.copy(servingsText = servings) }
    fun updateImageUri(uri: String) = _state.update { it.copy(imageUri = uri) }

    // Ingredients (dynamic list) update functions
    fun addIngredient() = _state.update {
        it.copy(ingredients = it.ingredients + RecipeIngredient())
    }

    fun removeIngredient(index: Int) = _state.update {
        val newList = it.ingredients.toMutableList().apply { removeAt(index) }
        it.copy(ingredients = if (newList.isEmpty()) listOf(RecipeIngredient()) else newList)
    }

    fun updateIngredientName(index: Int, name: String) = _state.update {
        val newList = it.ingredients.toMutableList()
        newList[index] = newList[index].copy(name = name)
        it.copy(ingredients = newList)
    }

    fun updateIngredientQuantity(index: Int, qtyStr: String) = _state.update {
        val qty = qtyStr.toIntOrNull() ?: 0
        val newList = it.ingredients.toMutableList()
        newList[index] = newList[index].copy(quantity = qty)
        it.copy(ingredients = newList)
    }

    fun updateIngredientUnit(index: Int, unit: String) = _state.update {
        val newList = it.ingredients.toMutableList()
        newList[index] = newList[index].copy(unit = unit)
        it.copy(ingredients = newList)
    }

    // Steps (dynamic list) update functions
    fun addStep() = _state.update {
        it.copy(steps = it.steps + "")
    }

    fun removeStep(index: Int) = _state.update {
        val newList = it.steps.toMutableList().apply { removeAt(index) }
        it.copy(steps = if (newList.isEmpty()) listOf("") else newList)
    }

    fun updateStep(index: Int, step: String) = _state.update {
        val newList = it.steps.toMutableList()
        newList[index] = step
        it.copy(steps = newList)
    }

    // Validation and Saving
    @OptIn(ExperimentalUuidApi::class)
    fun saveRecipe(onSuccess: () -> Unit) {
        val currentState = _state.value
        val errors = mutableMapOf<String, String>()

        // Validation
        if (currentState.name.isBlank()) errors["name"] = "Recipe name cannot be empty"

        val cookingTime = currentState.cookingTimeMinutesText.toIntOrNull()
        if (cookingTime == null || cookingTime <= 0) errors["cookingTime"] = "Enter a valid time"

        val servings = currentState.servingsText.toIntOrNull()
        if (servings == null || servings <= 0) errors["servings"] = "Enter a valid serving size"

        if (currentState.ingredients.isEmpty()) {
            errors["ingredients"] = "Add at least one ingredient"
        } else if (currentState.ingredients.any { it.name.isBlank() || it.unit.isBlank() || it.quantity <= 0 }) {
            errors["ingredients"] = "All ingredient fields must be filled or removed"
        }

        if (currentState.steps.isEmpty()) {
            errors["steps"] = "Add at least one step"
        } else if (currentState.steps.any { it.isBlank() }) {
            errors["steps"] = "All steps must be filled or removed"
        }

        // If there are errors show them and stop saving
        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors) }
            return
        }

        // Saving in DB
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Upload image
            val publicImageUrl = if (currentState.imageUri.isNotBlank()) {
                com.example.gustoria.data.utils.ImageUploader.uploadImage(currentState.imageUri, "recipes")
                    ?: currentState.imageUri
            } else null

            val baseRecipe = originalRecipe ?: Recipe(
                id = Uuid.random().toString(),
                ownerId = SessionManagerFacade.currentUserId.value ?: ""
            )

            val updatedRecipe = baseRecipe.copy(
                name = currentState.name,
                description = currentState.description,
                cost = currentState.cost,
                difficulty = currentState.difficulty,
                cookingTimeMinutes = cookingTime!!,
                servings = servings!!,
                ingredients = currentState.ingredients,
                steps = currentState.steps,
                imageUri = publicImageUrl
            )

            if (isEditMode) {
                recipeRepository.updateRecipe(updatedRecipe.id, updatedRecipe)
            } else {
                recipeRepository.addRecipe(updatedRecipe)
            }

            // Report to UI to go back (close screen)
            onSuccess()
        }
    }

    fun deleteRecipe(onSuccess: () -> Unit) {
        if (recipeId != null) {
            viewModelScope.launch {
                // 1. Elimina tutte le review associate
                val reviews = reviewRepository.getReviewsByRecipe(recipeId).first()
                reviews.forEach { reviewRepository.deleteReview(it.id) }

                // 2. Rimuovi dai preferiti di chi l'aveva
                userRepo.getUsersWhoHaveInFavourites(recipeId).forEach { user ->
                    userRepo.removeFavourite(user.internalId, recipeId)
                    recipeRepository.removeLikedByUser(recipeId, user.internalId)
                }

                // 3. Rimuovi dai tried di chi ce l'aveva
                userRepo.getUsersWhoHaveTried(recipeId).forEach { user ->
                    userRepo.removeTriedRecipe(user.internalId, recipeId)
                }

                // 4. Elimina le notifiche collegate a questa ricetta
                notificationRepo.deleteNotificationsForRecipe(recipeId)

                // 5. Elimina la ricetta
                recipeRepository.deleteRecipe(recipeId)
                onSuccess()
            }
        }
    }

    // Factory used to create the ViewModel
    companion object {
        fun factory(
            recipeId: String?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val recipeRepository = application.container.recipeRepository
                val userRepository = application.container.userRepository
                val reviewRepository = application.container.reviewRepository
                val notificationRepository = application.container.notificationRepository
                EditRecipeViewModel(recipeRepository, userRepository, reviewRepository, notificationRepository, recipeId)
            }
        }
    }
}