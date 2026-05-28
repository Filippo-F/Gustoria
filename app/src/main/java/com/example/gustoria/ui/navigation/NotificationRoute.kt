package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import com.example.gustoria.ui.notifications.NotificationScreen
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.viewmodel.NotificationViewModel


@Serializable
object Notifications

@Composable
fun NotificationsDestination(
    navActions: GustoriaNavigationActions,
    viewModel: NotificationViewModel = viewModel(factory = NotificationViewModel.Factory)
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    NotificationScreen(
        notifications = notifications,
        onBack = navActions::navigateBack,
        onNotificationClick = { recipeId ->
            viewModel.markAsRead(
                notifications.find { it.targetRecipeId == recipeId }?.id ?: ""
            )
            navActions.navigateToRecipeDetails(recipeId)
        },
        onDeleteNotification = viewModel::deleteNotification,
        onMarkAllRead = viewModel::markAllAsRead
    )
}