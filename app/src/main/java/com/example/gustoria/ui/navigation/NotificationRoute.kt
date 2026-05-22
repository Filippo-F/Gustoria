package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import com.example.gustoria.ui.notifications.NotificationScreen
import kotlinx.serialization.Serializable

@Serializable
object Notifications

@Composable
fun NotificationsDestination(
    navActions: GustoriaNavigationActions
) {
    NotificationScreen(
        onBack = navActions::navigateBack
    )
}
