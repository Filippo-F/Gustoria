package com.example.gustoria.dataclass

import com.google.firebase.firestore.PropertyName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class NotificationType {
    RECIPE_DUPLICATED, // someone duplicated your recipe
    REVIEW_RECEIVED, // someone reviewed your recipe
    RECOMMENDED_RECIPE, // recommended recipe
    RECIPE_SAVED, // your recipe was saved by another user
    REVIEW_LIKED, // someone liked your review
    NEW_FOLLOWER
}

@OptIn(ExperimentalUuidApi::class)
data class Notification(
    val id: String = Uuid.random().toString(),
    val recipientUserId: String = "",
    val type: String = NotificationType.RECOMMENDED_RECIPE.name,
    val title: String = "",
    val message: String = "",
    val targetRecipeId: String? = null, // for navigation on tap
    val targetUserId: String? = null,
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)