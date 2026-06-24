package com.example.gustoria.domain

import com.example.gustoria.dataclass.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepoInterface {
    fun getNotificationsForUser(userId: String): Flow<List<Notification>>
    suspend fun addNotification(notification: Notification)
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead(userId: String)
    suspend fun deleteNotification(notificationId: String)
    // Elimina tutte le notifiche che fanno riferimento a una ricetta (cascade delete)
    suspend fun deleteNotificationsForRecipe(recipeId: String)
    // Elimina tutte le notifiche ricevute da un utente (quando disattiva le push notifications)
    suspend fun deleteAllNotificationsForUser(userId: String)
}