package com.example.gustoria.viewmodel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Local preferences
    private val prefs = application.getSharedPreferences("gustoria_settings", Context.MODE_PRIVATE)

    var selectedTheme by mutableIntStateOf(prefs.getInt("selected_theme", 2))
        private set

    var fontSize by mutableFloatStateOf(prefs.getFloat("font_size", 0.5f))
        private set

    var unitMeasure by mutableIntStateOf(prefs.getInt("unit_measure", 0))
        private set

    // TODO: Account preferences (Awaiting Firestore Integration) ---
    // Default to 'true' on fresh install, but eventually overwritten
    // when the user logs in and their Firestore profile is fetched.
    var pushNotificationsEnabled by mutableStateOf(true)
        private set

    var newRecipeAlertsEnabled by mutableStateOf(true)
        private set

    var gustoriaWeeklyEnabled by mutableStateOf(false)
        private set


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

    fun updateUnitMeasure(index: Int) {
        unitMeasure = index
        prefs.edit { putInt("unit_measure", index) }
    }


    // TODO: ACCOUNT SETTINGS UPDATE FUNCTIONS
    // Right now just update the UI.
    // To later implement: push these changes to Firebase
    fun togglePushNotifications() {
        pushNotificationsEnabled = !pushNotificationsEnabled
    }

    fun toggleNewRecipeAlerts() {
        newRecipeAlertsEnabled = !newRecipeAlertsEnabled
    }

    fun toggleGustoriaWeekly() {
        gustoriaWeeklyEnabled = !gustoriaWeeklyEnabled
    }
}