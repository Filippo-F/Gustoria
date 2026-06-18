package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.data.utils.ImageUploader
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.ui.recipe.RecipeFilters
import com.example.gustoria.ui.recipe.applyFilters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalUuidApi::class)
class RecipeCollectionViewModel(
    private val repo: RecipeRepoInterface,
    private val userRepo: UserRepoInterface,
    private val reviewRepository: ReviewRepoInterface,
    private val notificationRepo: NotificationRepoInterface
) : ViewModel() {

    private val userId get() = SessionManagerFacade.currentUserId.value ?: ""

    private val _filters = MutableStateFlow(RecipeFilters())
    val filters: StateFlow<RecipeFilters> = _filters.asStateFlow()
    // 1. Saved Recipes
    private val savedRecipes: StateFlow<List<Recipe>> = combine(
        repo.getAllRecipes(),
        userRepo.getFavouriteRecipeIds(userId)
    ) { allRecipes, favouriteIds ->
        allRecipes.filter { it.id in favouriteIds }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 2. Tried Recipes
    private val triedRecipes: StateFlow<List<Recipe>> = combine(
        repo.getAllRecipes(),
        userRepo.getTriedRecipeIds(userId)
    ) { allRecipes, triedIds ->
        allRecipes.filter { it.id in triedIds }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 3. Created Recipes (Owned by User)
    private val createdRecipes: StateFlow<List<Recipe>> = repo.getRecipeByOwner(userId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Keep track of current tab (0: Saved, 1: Tried, 2: Created)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _selectedRecipeId = MutableStateFlow<String?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipeId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repo.getRecipeById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setTab(index: Int) {
        _currentTab.value = index
        resetFilters()
    }

    // Dynamically change list based on active tab and filters
    val recipesToShow: StateFlow<List<Recipe>> = combine(
        savedRecipes, triedRecipes, createdRecipes, _currentTab, _filters
    ) { saved, tried, created, tabIndex, f ->
        val baseList = when (tabIndex) {
            0 -> saved
            1 -> tried
            else -> created
        }
        baseList.applyFilters(f)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setNameQuery(q: String) { _filters.update { it.copy(nameQuery = q) } }
    fun setIngredientQuery(q: String) { _filters.update { it.copy(ingredientQuery = q) } }
    fun toggleCost(c: String) {
        _filters.update {
            val newSet = if (c in it.selectedCosts) it.selectedCosts - c else it.selectedCosts + c
            it.copy(selectedCosts = newSet)
        }
    }
    fun toggleDifficulty(d: String) {
        _filters.update {
            val newSet = if (d in it.selectedDifficulties) it.selectedDifficulties - d else it.selectedDifficulties + d
            it.copy(selectedDifficulties = newSet)
        }
    }
    fun toggleCuisine(cuisine: String) {
        _filters.update {
            val newSet = if (cuisine in it.selectedCuisines) it.selectedCuisines - cuisine else it.selectedCuisines + cuisine
            it.copy(selectedCuisines = newSet)
        }
    }
    fun toggleMealType(meal: String) {
        _filters.update {
            val newSet = if (meal in it.selectedMealTypes) it.selectedMealTypes - meal else it.selectedMealTypes + meal
            it.copy(selectedMealTypes = newSet)
        }
    }
    fun toggleDietaryTag(tag: String) {
        _filters.update {
            val newSet = if (tag in it.selectedDietaryTags) it.selectedDietaryTags - tag else it.selectedDietaryTags + tag
            it.copy(selectedDietaryTags = newSet)
        }
    }
    fun setMaxCookingTime(minutes: Int?) {
        _filters.update { it.copy(maxCookingTimeMinutes = minutes) }
    }
    fun resetFilters() { _filters.value = RecipeFilters() }

    fun selectRecipe(recipeId: String?) {
        _selectedRecipeId.value = recipeId
    }

    fun isOwnedByCurrentUser(recipe: Recipe): Boolean {
        return recipe.ownerId == userId
    }

    fun delete(id: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            // 0. Get recipe for image cleanup
            val recipe = repo.getRecipeById(id).first()

            // 1. Delete associated reviews
            val reviews = reviewRepository.getReviewsByRecipe(id).first()
            reviews.forEach { reviewRepository.deleteReview(it.id) }

            // 2. Remove from user favorites
            userRepo.getUsersWhoHaveInFavourites(id).forEach { user ->
                userRepo.removeFavourite(user.internalId, id)
                repo.removeLikedByUser(id, user.internalId)
            }

            // 3. Remove from user tried recipes
            userRepo.getUsersWhoHaveTried(id).forEach { user ->
                userRepo.removeTriedRecipe(user.internalId, id)
            }

            // 4. Delete associated notifications
            notificationRepo.deleteNotificationsForRecipe(id)

            // 5. Delete image
            recipe?.imageUri?.let {
                ImageUploader.deleteImage(it, "recipes")
            }

            // 6. Delete recipe
            repo.deleteRecipe(id)
            onSuccess()
        }
    }

    fun duplicateRecipe(recipe: Recipe, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            val newId = Uuid.random().toString()
            val duplicatedRecipe = recipe.copy(
                id = newId,
                ownerId = userId,
                name = if (recipe.name.endsWith(" (Copy)")) recipe.name else "${recipe.name} (Copy)",
                imageUri = null // Reset image for the duplicated recipe
            )
            repo.addRecipe(duplicatedRecipe)

            // Notify original recipe owner
            if (recipe.ownerId != userId) {
                notificationRepo.addNotification(
                    Notification(
                        recipientUserId = recipe.ownerId,
                        type = NotificationType.RECIPE_DUPLICATED.name,
                        title = "Your recipe was duplicated!",
                        message = "\"${duplicatedRecipe.name}\" was inspired by your recipe.",
                        targetRecipeId = duplicatedRecipe.id
                    )
                )
            }

            onSuccess(newId)
        }
    }

    fun isFavouriteFlow(recipeId: String): Flow<Boolean> =
        userRepo.isFavourite(userId, recipeId)
    fun toggleFavourite(recipeId: String) {
        viewModelScope.launch {
            val isFav = userRepo
                .isFavourite(userId, recipeId)
                .first()  // Read current value once
            if (isFav) {
                userRepo.removeFavourite(userId, recipeId)
                repo.removeLikedByUser(recipeId, userId)
            } else {
                userRepo.addFavourite(userId, recipeId)
                repo.addLikedByUser(recipeId, userId)
            }
        }
    }

    fun isTriedFlow(recipeId: String): Flow<Boolean> =
        userRepo.isTried(userId, recipeId)

    fun toggleTried(recipeId: String) {
        viewModelScope.launch {
            val tried = userRepo.isTried(userId, recipeId).first()
            if (tried) userRepo.removeTriedRecipe(userId, recipeId)
            else userRepo.addTriedRecipe(userId, recipeId)
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val recipeRepository = application.container.recipeRepository
                val userRepository = application.container.userRepository
                val reviewRepository = application.container.reviewRepository
                val notificationRepository = application.container.notificationRepository
                RecipeCollectionViewModel(recipeRepository, userRepository, reviewRepository, notificationRepository)
            }
        }
    }
}