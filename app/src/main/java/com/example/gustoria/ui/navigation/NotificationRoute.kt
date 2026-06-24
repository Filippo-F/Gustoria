package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import com.example.gustoria.ui.notifications.NotificationScreen
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.viewmodel.NotificationViewModel
import com.example.gustoria.dataclass.NotificationType


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
        onNotificationClick = { notification ->
            viewModel.markAsRead(notification.id)
            when (notification.type) {
                NotificationType.NEW_FOLLOWER.name -> {
                    notification.targetUserId?.let { navActions.navigateToOtherProfile(it) }
                }
                NotificationType.REVIEW_RECEIVED.name, NotificationType.REVIEW_LIKED.name -> {
                    notification.targetRecipeId?.let {
                        // Stack navigation: go to recipe details first, then to reviews list
                        // so that "back" from reviews goes to recipe details.
                        navActions.navigateToRecipeDetails(it)
                        navActions.navigateToReviewsList(it)
                    }
                }
                else -> {
                    notification.targetRecipeId?.let { navActions.navigateToRecipeDetails(it) }
                }
            }
        },
        onDeleteNotification = viewModel::deleteNotification,
        onMarkAllRead = viewModel::markAllAsRead,
        onMarkRead = viewModel::markAsRead
    )
}