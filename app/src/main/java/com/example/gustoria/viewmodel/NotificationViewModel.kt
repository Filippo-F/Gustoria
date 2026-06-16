package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModel(
    private val notificationRepo: NotificationRepoInterface,
    private val recipeRepo: RecipeRepoInterface,
    private val userRepo: UserRepoInterface
) : ViewModel() {

    private val currentUserId: String
        get() = SessionManagerFacade.currentUserId.value ?: ""

    // (Updated when user is logged in)
    val notifications: StateFlow<List<Notification>> = SessionManagerFacade.currentUserId
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) flowOf(emptyList())
            else notificationRepo.getNotificationsForUser(uid)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val unreadCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    init {
        generateRecommendedNotifications()
    }

    // Generate "Recommended" notifications for recipes matching user preferences
    private fun generateRecommendedNotifications() {
        viewModelScope.launch {
            val user = userRepo.getUserById(currentUserId).first() ?: return@launch
            val userPrefs = (
                    user.cuisinePreferences +
                            user.dietaryRestrictions +
                            user.favoriteIngredients
                    ).map { it.lowercase() }
                .filter { it.isNotBlank() }
                .toSet()

            if (userPrefs.isEmpty()) return@launch

            val allRecipes = recipeRepo.getAllRecipes().first()

            allRecipes
                .filter { recipe -> recipe.ownerId != currentUserId }
                .forEach { recipe ->
                    // prendo tutti i campi
                    val recipeAttributes = buildList {
                        add(recipe.cuisineType)
                        add(recipe.mealType)
                        addAll(recipe.dietaryTags)
                        addAll(recipe.tags)
                        addAll(recipe.ingredients.map { it.name })
                    }.map { it.lowercase() }
                        .filter { it.isNotBlank() }

                    val matches = recipeAttributes.any { attr ->
                        userPrefs.any { pref ->
                            attr.contains(pref) || pref.contains(attr)
                        }
                    }

                    if (matches) {
                        notificationRepo.addNotification(
                            Notification(
                                recipientUserId = currentUserId,
                                type = NotificationType.RECOMMENDED_RECIPE.name,
                                title = "Recommended for you",
                                message = "\"${recipe.name}\" matches your taste preferences!",
                                targetRecipeId = recipe.id
                            )
                        )
                    }
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch { notificationRepo.markAsRead(notificationId) }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch { notificationRepo.deleteNotification(notificationId) }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepo.markAllAsRead(currentUserId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as GustoriaApplication)
                NotificationViewModel(
                    notificationRepo = app.container.notificationRepository,
                    recipeRepo = app.container.recipeRepository,
                    userRepo = app.container.userRepository
                )
            }
        }
    }
}