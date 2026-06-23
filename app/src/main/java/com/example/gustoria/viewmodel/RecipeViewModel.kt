package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.data.utils.ImageUploader
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.recipe.RecipeFilters
import com.example.gustoria.ui.recipe.applyFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.ReviewRepoInterface
import kotlinx.coroutines.flow.flowOf

class RecipeViewModel(
    private val recipeRepository: RecipeRepoInterface,
    private val userRepo: UserRepoInterface,
    private val notificationRepo: NotificationRepoInterface,
    private val reviewRepository: ReviewRepoInterface
) : ViewModel() {

    val recipes: StateFlow<List<Recipe>> = recipeRepository.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _filters = MutableStateFlow(RecipeFilters())
    val filters: StateFlow<RecipeFilters> = _filters.asStateFlow()

    val filteredRecipes: StateFlow<List<Recipe>> =
        combine(recipes, _filters) { list, filters -> list.applyFilters(filters) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _selectedRecipeId = MutableStateFlow<String?>(null)
    val selectedRecipe: StateFlow<Recipe?> =
        combine(recipes, _selectedRecipeId) { list, id -> list.find { it.id == id } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )


    fun isOwnedByCurrentUser(recipe: Recipe): Boolean =
        recipe.ownerId == (SessionManagerFacade.currentUserId.value ?: "")
    fun selectRecipe(recipeId: String?) {
        _selectedRecipeId.value = recipeId
    }

    fun getAuthor(userId: String) = userRepo.getUserById(userId)


    fun updateNameQuery(query: String) =
        _filters.update { it.copy(nameQuery = query) }

    fun updateIngredientQuery(query: String) =
        _filters.update { it.copy(ingredientQuery = query) }

    fun toggleDifficulty(difficulty: String) =
        _filters.update {
            val newSet = if (difficulty in it.selectedDifficulties) it.selectedDifficulties - difficulty
            else it.selectedDifficulties + difficulty
            it.copy(selectedDifficulties = newSet)
        }

    fun toggleCost(cost: String) =
        _filters.update {
            val newSet = if (cost in it.selectedCosts) it.selectedCosts - cost
            else it.selectedCosts + cost
            it.copy(selectedCosts = newSet)
        }

    fun toggleCuisine(cuisine: String) =
        _filters.update {
            val newSet = if (cuisine in it.selectedCuisines) it.selectedCuisines - cuisine
            else it.selectedCuisines + cuisine
            it.copy(selectedCuisines = newSet)
        }

    fun toggleMealType(mealType: String) =
        _filters.update {
            val newSet = if (mealType in it.selectedMealTypes) it.selectedMealTypes - mealType
            else it.selectedMealTypes + mealType
            it.copy(selectedMealTypes = newSet)
        }

    fun toggleDietaryTag(tag: String) =
        _filters.update {
            val newSet = if (tag in it.selectedDietaryTags) it.selectedDietaryTags - tag
            else it.selectedDietaryTags + tag
            it.copy(selectedDietaryTags = newSet)
        }

    fun setMaxCookingTime(minutes: Int?) =
        _filters.update { it.copy(maxCookingTimeMinutes = minutes) }

    fun resetFilters() {
        _filters.value = RecipeFilters()
    }

    fun setFilters(filters: RecipeFilters) {
        _filters.value = filters
    }

    fun isFavouriteFlow(recipeId: String): Flow<Boolean> {
        val userId = SessionManagerFacade.currentUserId.value
        return if (userId.isNullOrBlank()) {
            flowOf(false)
        } else {
            userRepo.isFavourite(userId, recipeId)
        }
    }

    fun toggleFavourite(recipeId: String) {
        viewModelScope.launch {
            val userId = SessionManagerFacade.currentUserId.value ?: ""
            val isFav = userRepo.isFavourite(userId, recipeId).first()
            if (isFav) {
                userRepo.removeFavourite(userId, recipeId)
                recipeRepository.removeLikedByUser(recipeId, userId)
            } else {
                userRepo.addFavourite(userId, recipeId)
                recipeRepository.addLikedByUser(recipeId, userId)
                // Notify recipe owner
                recipeRepository.getRecipeById(recipeId).first()?.let { recipe ->
                    if (recipe.ownerId != userId) {
                        notificationRepo.addNotification(
                            Notification(
                                recipientUserId = recipe.ownerId,
                                type = NotificationType.RECIPE_SAVED.name,
                                title = "Someone saved your recipe",
                                message = "\"${recipe.name}\" was added to someone's favourites.",
                                targetRecipeId = recipeId
                            )
                        )
                    }
                }
            }
        }
    }


    fun isTriedFlow(recipeId: String): Flow<Boolean> {
        val userId = SessionManagerFacade.currentUserId.value
        return if (userId.isNullOrBlank()) {
            flowOf(false)
        } else {
            userRepo.isTried(userId, recipeId)
        }
    }

    fun toggleTried(recipeId: String) {
        viewModelScope.launch {
            val userId = SessionManagerFacade.currentUserId.value ?: ""
            val hasTried = userRepo.isTried(userId, recipeId).first()
            if (hasTried) userRepo.removeTriedRecipe(userId, recipeId)
            else userRepo.addTriedRecipe(userId, recipeId)
        }
    }
    fun deleteRecipe(recipeId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            // 0. Get recipe to find image URL
            val recipe = recipeRepository.getRecipeById(recipeId).first()

            // 1. Delete associated reviews
            val reviews = reviewRepository.getReviewsByRecipe(recipeId).first()
            reviews.forEach { reviewRepository.deleteReview(it.id) }

            // 2. Remove from user favorites
            userRepo.getUsersWhoHaveInFavourites(recipeId).forEach { user ->
                userRepo.removeFavourite(user.internalId, recipeId)
                recipeRepository.removeLikedByUser(recipeId, user.internalId)
            }

            // 3. Remove from user tried recipes
            userRepo.getUsersWhoHaveTried(recipeId).forEach { user ->
                userRepo.removeTriedRecipe(user.internalId, recipeId)
            }

            // 4. Delete associated notifications
            notificationRepo.deleteNotificationsForRecipe(recipeId)

            // 5. Delete image from storage
            recipe?.imageUri?.let {
                ImageUploader.deleteImage(it, "recipes")
            }

            // 6. Delete recipe
            recipeRepository.deleteRecipe(recipeId)
            selectRecipe(null)
            onSuccess()
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    fun duplicateRecipe(recipe: Recipe, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            val newId = Uuid.random().toString()
            val copy = recipe.copy(
                id = newId,
                ownerId = SessionManagerFacade.currentUserId.value ?: "",
                name = if (recipe.name.endsWith(" (Copy)")) recipe.name
                else "${recipe.name} (Copy)",
                imageUri = null, // Reset image for the duplicated recipe
                likedByUserIds = emptyList() // Reset likes for the new recipe
            )
            recipeRepository.addRecipe(copy)

            // Notify original recipe owner
            if (recipe.ownerId != (SessionManagerFacade.currentUserId.value ?: "")) {
                notificationRepo.addNotification(
                    Notification(
                        recipientUserId = recipe.ownerId,
                        type = NotificationType.RECIPE_DUPLICATED.name,
                        title = "Your recipe was duplicated!",
                        message = "\"${copy.name}\" was inspired by your recipe.",
                        targetRecipeId = copy.id
                    )
                )
            }

            onSuccess(newId)
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val recipeRepository = application.container.recipeRepository
                val userRepository = application.container.userRepository
                val notificationRepository = application.container.notificationRepository
                val reviewRepository = application.container.reviewRepository
                RecipeViewModel(recipeRepository, userRepository, notificationRepository, reviewRepository)
            }
        }
    }
    }
