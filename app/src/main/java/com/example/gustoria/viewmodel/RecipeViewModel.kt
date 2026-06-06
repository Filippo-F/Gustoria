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

    fun resetFilters() {
        _filters.value = RecipeFilters()
    }

    fun isFavouriteFlow(recipeId: String): Flow<Boolean> =
        userRepo.isFavourite((SessionManagerFacade.currentUserId.value ?: ""), recipeId)

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
                // Notifica il proprietario della ricetta
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


    fun isTriedFlow(recipeId: String): Flow<Boolean> =
        userRepo.isTried((SessionManagerFacade.currentUserId.value ?: ""), recipeId)

    fun toggleTried(recipeId: String) {
        viewModelScope.launch {
            val userId = SessionManagerFacade.currentUserId.value ?: ""
            val hasTried = userRepo.isTried(userId, recipeId).first()
            if (hasTried) userRepo.removeTriedRecipe(userId, recipeId)
            else userRepo.addTriedRecipe(userId, recipeId)
        }
    }
    fun deleteRecipe(recipeId: String) {
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
            selectRecipe(null)
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
                else "${recipe.name} (Copy)"
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
