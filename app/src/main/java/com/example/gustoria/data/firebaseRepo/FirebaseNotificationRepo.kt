package com.example.gustoria.data.firebaseRepo

import com.example.gustoria.dataclass.Notification
import com.example.gustoria.domain.Collections
import com.example.gustoria.domain.NotificationRepoInterface
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.example.gustoria.dataclass.NotificationType

class FirebaseNotificationRepo(
    private val firestore: FirebaseFirestore
) : NotificationRepoInterface {

    private val notificationsCollection = firestore.collection(Collections.NOTIFICATIONS)

    suspend fun initializeData() {
        // real notifications are generated dynamically by user actions
    }

    override fun getNotificationsForUser(userId: String): Flow<List<Notification>> {
        return notificationsCollection
            .whereEqualTo("recipientUserId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Notification::class.java)
            }
    }

    override suspend fun addNotification(notification: Notification) {
        if (notification.type == NotificationType.RECOMMENDED_RECIPE.name) {
            val existing = notificationsCollection
                .whereEqualTo("recipientUserId", notification.recipientUserId)
                .whereEqualTo("type", notification.type)
                .whereEqualTo("targetRecipeId", notification.targetRecipeId)
                .get().await()
            if (!existing.isEmpty) return
        }
        notificationsCollection.document(notification.id).set(notification).await()
    }

    override suspend fun markAsRead(notificationId: String) {
        notificationsCollection.document(notificationId).update("isRead", true).await()
    }

    override suspend fun markAllAsRead(userId: String) {
        val snapshot = notificationsCollection
            .whereEqualTo("recipientUserId", userId)
            .whereEqualTo("isRead", false)
            .get().await()

        if (!snapshot.isEmpty) {
            firestore.runBatch { batch ->
                snapshot.documents.forEach { doc ->
                    batch.update(doc.reference, "isRead", true)
                }
            }.await()
        }
    }

    override suspend fun deleteNotification(notificationId: String) {
        notificationsCollection.document(notificationId).delete().await()
    }

    override suspend fun deleteNotificationsForRecipe(recipeId: String) {
        val snapshot = notificationsCollection
            .whereEqualTo("targetRecipeId", recipeId)
            .get().await()
        firestore.runBatch { batch ->
            snapshot.documents.forEach { batch.delete(it.reference) }
        }.await()
    }

    override suspend fun deleteAllNotificationsForUser(userId: String) {
        val snapshot = notificationsCollection
            .whereEqualTo("recipientUserId", userId)
            .get().await()
        if (!snapshot.isEmpty) {
            firestore.runBatch { batch ->
                snapshot.documents.forEach { batch.delete(it.reference) }
            }.await()
        }
    }

    override suspend fun deleteRecommendedNotificationsForUser(userId: String) {
        val snapshot = notificationsCollection
            .whereEqualTo("recipientUserId", userId)
            .whereEqualTo("type", NotificationType.RECOMMENDED_RECIPE.name)
            .get().await()
        if (!snapshot.isEmpty) {
            firestore.runBatch { batch ->
                snapshot.documents.forEach { batch.delete(it.reference) }
            }.await()
        }
    }
}
