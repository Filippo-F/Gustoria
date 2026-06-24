package com.example.gustoria.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel(
    application: Application,
    private val userRepo: UserRepoInterface,
    private val notificationRepo: NotificationRepoInterface
) : AndroidViewModel(application) {

    // Local preferences
    private val prefs = application.getSharedPreferences("gustoria_settings", Context.MODE_PRIVATE)

    var selectedTheme by mutableIntStateOf(prefs.getInt("selected_theme", 2))
        private set

    var fontSize by mutableFloatStateOf(prefs.getFloat("font_size", 0.5f))
        private set

    // Account preferences — kept in sync with Firestore via the current user's Flow
    var pushNotificationsEnabled by mutableStateOf(true)
        private set

    init {
        // Whenever the logged-in user changes (or on first load), mirror the Firestore values
        viewModelScope.launch {
            SessionManagerFacade.currentUserId
                .flatMapLatest { uid ->
                    if (uid.isNullOrBlank()) flowOf(null)
                    else userRepo.getUserById(uid)
                }
                .collect { user ->
                    user?.let {
                        pushNotificationsEnabled = it.pushNotificationsEnabled
                    }
                }
        }
    }

    // Local settings update functions
    fun updateTheme(themeIndex: Int) {
        selectedTheme = themeIndex
        prefs.edit { putInt("selected_theme", themeIndex) }
    }

    fun updateFontSize(value: Float) {
        fontSize = value
    }

    fun saveFontSizeToDisk() {
        prefs.edit { putFloat("font_size", fontSize) }
    }

    // Account settings update functions — optimistic UI + Firestore persistence
    fun togglePushNotifications() {
        val newValue = !pushNotificationsEnabled
        pushNotificationsEnabled = newValue
        val uid = SessionManagerFacade.currentUserId.value!!
        viewModelScope.launch {
            userRepo.updatePushNotificationsEnabled(uid, newValue)
            if (!newValue) {
                // Delete all received notifications when disabling push notifications
                notificationRepo.deleteAllNotificationsForUser(uid)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val userRepository = application.container.userRepository
                val notificationRepository = application.container.notificationRepository
                SettingsViewModel(application, userRepository, notificationRepository)
            }
        }
    }
}