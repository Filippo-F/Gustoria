package com.example.gustoria.data.paperRepo

import com.example.gustoria.dataclass.Notification
import com.example.gustoria.domain.Collections
import com.example.gustoria.domain.NotificationRepoInterface
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class PaperNotificationRepo : NotificationRepoInterface {

    private val book = Paper.book(Collections.NOTIFICATIONS)

    private val _notifications = MutableStateFlow<List<Notification>>(loadAll())

    private fun loadAll(): List<Notification> {
        return try {
            book.allKeys.mapNotNull { book.read<Notification>(it) }
        } catch (e: Exception) {
            book.destroy()
            emptyList()
        }
    }

    override fun getNotificationsForUser(userId: String): Flow<List<Notification>> =
        _notifications
            .map { list ->
                list
                    .filter { it.recipientUserId == userId }
                    .sortedByDescending { it.timestamp }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun addNotification(notification: Notification) =
        withContext(Dispatchers.IO) {
            // Evita duplicati
            val alreadyExists = _notifications.value.any {
                it.recipientUserId == notification.recipientUserId &&
                        it.type == notification.type &&
                        it.targetRecipeId == notification.targetRecipeId
            }
            if (!alreadyExists) {
                book.write(notification.id, notification)
                _notifications.update { it + notification }
            }
        }

    override suspend fun markAsRead(notificationId: String) =
        withContext(Dispatchers.IO) {
            _notifications.update { list ->
                list.map { n ->
                    if (n.id == notificationId) {
                        val updated = n.copy(isRead = true)
                        book.write(updated.id, updated)
                        updated
                    } else n
                }
            }
        }

    override suspend fun deleteNotification(notificationId: String) =
        withContext(Dispatchers.IO) {
            book.delete(notificationId)
            _notifications.update { it.filter { n -> n.id != notificationId } }
        }
}
