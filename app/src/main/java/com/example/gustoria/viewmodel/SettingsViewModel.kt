package com.example.gustoria.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var selectedTheme by mutableIntStateOf(2) // 0: Light, 1: Dark, 2: Auto
        private set

    var brightness by mutableFloatStateOf(0.5f)
        private set

    var fontSize by mutableFloatStateOf(0.5f)
        private set

    var pushNotificationsEnabled by mutableStateOf(true)
        private set

    var newRecipeAlertsEnabled by mutableStateOf(true)
        private set

    var gustoriaWeeklyEnabled by mutableStateOf(false)
        private set

    var unitMeasure by mutableIntStateOf(0) // 0: Metric, 1: Imperial
        private set

    fun updateTheme(themeIndex: Int) {
        selectedTheme = themeIndex
    }

    fun updateBrightness(value: Float) {
        brightness = value
    }

    fun updateFontSize(value: Float) {
        fontSize = value
    }

    fun togglePushNotifications() {
        pushNotificationsEnabled = !pushNotificationsEnabled
    }

    fun toggleNewRecipeAlerts() {
        newRecipeAlertsEnabled = !newRecipeAlertsEnabled
    }

    fun toggleGustoriaWeekly() {
        gustoriaWeeklyEnabled = !gustoriaWeeklyEnabled
    }

    fun updateUnitMeasure(index: Int) {
        unitMeasure = index
    }
}
