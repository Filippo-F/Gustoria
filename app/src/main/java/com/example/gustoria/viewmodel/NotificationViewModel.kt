package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.data.auth.SessionManager
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepo: NotificationRepoInterface,
    private val recipeRepo: RecipeRepoInterface,
    private val userRepo: UserRepoInterface
) : ViewModel() {

    // Usa Firebase UID se disponibile, altrimenti placeholder ((temporaneo, da cambiare!!!!))
    private val currentUserId: String
        get() = SessionManagerFacade.currentUserId ?: SessionManager.CURRENT_LOGGED_IN_USER_ID

    val notifications: StateFlow<List<Notification>> =
        notificationRepo.getNotificationsForUser(currentUserId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val unreadCount: StateFlow<Int> =
        notificationRepo.getNotificationsForUser(currentUserId)
            .map { list -> list.count { !it.isRead } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    init {
        generateRecommendedNotifications()
    }

    //Controlla tutte le ricette disponibili e per quelle i cui tag matchano
    //le preferenze dell'utente, crea notifica "Recommended" (se non esiste già)

    private fun generateRecommendedNotifications() {
        viewModelScope.launch {
            val user = userRepo.getUserById(currentUserId).first() ?: return@launch
            val userPrefs = (
                    user.cuisinePreferences +
                            user.dietaryRestrictions +
                            user.favoriteIngredients
                    ).map { it.lowercase() }.toSet()

            if (userPrefs.isEmpty()) return@launch

            val allRecipes = recipeRepo.getAllRecipes().first()

            allRecipes
                .filter { recipe -> recipe.ownerId != currentUserId }
                .forEach { recipe ->
                    val recipeTags = recipe.tags.map { it.lowercase() }
                    val matches = recipeTags.any { tag ->
                        userPrefs.any { pref ->
                            tag.contains(pref) || pref.contains(tag)
                        }
                    }
                    if (matches) {
                        notificationRepo.addNotification(
                            Notification(
                                recipientUserId = currentUserId,
                                type = NotificationType.RECOMMENDED_RECIPE,
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
            notifications.value
                .filter { !it.isRead }
                .forEach { notificationRepo.markAsRead(it.id) }
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