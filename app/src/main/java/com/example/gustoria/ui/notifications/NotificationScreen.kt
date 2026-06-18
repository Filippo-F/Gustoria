package com.example.gustoria.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import java.util.Locale
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.gustoria.ui.utils.formatTimestamp

@MultiPreview
@Preview
@Composable
fun NotificationScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        NotificationScreen(
            notifications = listOf(
                Notification(
                    recipientUserId = "101",
                    type = NotificationType.RECIPE_DUPLICATED.name,
                    title = "Your recipe was duplicated!",
                    message = "\"Pasta Carbonara (Copy)\" was created from your recipe.",
                    targetRecipeId = "recipe_123",
                    isRead = false
                ),
                Notification(
                    recipientUserId = "101",
                    type = NotificationType.REVIEW_RECEIVED.name,
                    title = "New review on your recipe",
                    message = "Someone reviewed \"Lasagna Bolognese\".",
                    targetRecipeId = "recipe_456",
                    isRead = true
                ),
                Notification(
                    recipientUserId = "101",
                    type = NotificationType.RECOMMENDED_RECIPE.name,
                    title = "Recommended for you",
                    message = "\"Vegan Tacos\" matches your taste preferences!",
                    targetRecipeId = "recipe_789",
                    isRead = true
                )
            ),
            onBack = {},
            onNotificationClick = {},
            onDeleteNotification = {},
            onMarkAllRead = {},
            onMarkRead = {}
        )
    }
}

@Composable
fun NotificationScreen(
    notifications: List<Notification>,
    onBack: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    onDeleteNotification: (notificationId: String) -> Unit,
    onMarkAllRead: () -> Unit,
    onMarkRead: (notificationId: String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val unreadCount = notifications.count { !it.isRead }

    // Mostra snackbar alla prima apertura se ci sono notifiche non lette
    LaunchedEffect(Unit) {
        if (unreadCount > 0) {
            snackbarHostState.showSnackbar(
                message = "You have $unreadCount unread notification${if (unreadCount > 1) "s" else ""}",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                title = "Notifications",
                onBack = onBack,
                modifier = Modifier.height(56.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "No notifications yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Header con "Mark all as read" se ci sono non lette
                if (unreadCount > 0) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onMarkAllRead) {
                                Text(
                                    text = "Mark all as read",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                items(notifications, key = { it.id }) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { onNotificationClick(notification) },
                        onDelete = { onDeleteNotification(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val bgColor = if (!notification.isRead)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else
        MaterialTheme.colorScheme.surface

    val (icon, iconTint) = when (notification.type) {
        NotificationType.RECIPE_DUPLICATED.name  -> Icons.Filled.ContentCopy to MaterialTheme.colorScheme.tertiary
        NotificationType.REVIEW_RECEIVED.name    -> Icons.Filled.RateReview  to MaterialTheme.colorScheme.primary
        NotificationType.RECIPE_SAVED.name       -> Icons.Filled.Favorite    to MaterialTheme.colorScheme.error
        NotificationType.REVIEW_LIKED.name       -> Icons.Filled.ThumbUp     to MaterialTheme.colorScheme.primary
        NotificationType.NEW_FOLLOWER.name -> Icons.Filled.PersonAdd to MaterialTheme.colorScheme.tertiary
        else                                     -> Icons.Filled.Recommend   to MaterialTheme.colorScheme.secondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icona tipo notifica
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        // Testo
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = notification.title,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                // Pallino rosso se non letta
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatTimestamp(notification.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }

        // Bottone elimina
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete notification",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}
