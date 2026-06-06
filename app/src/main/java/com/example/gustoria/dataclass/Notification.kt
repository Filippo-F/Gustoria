package com.example.gustoria.dataclass

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class NotificationType {
    RECIPE_DUPLICATED,   // qualcuno ha duplicato una tua ricetta
    REVIEW_RECEIVED,     // qualcuno ha recensito una tua ricetta
    RECOMMENDED_RECIPE,   // ricetta consigliata
    RECIPE_SAVED,      // (tua) ricetta salvata da un altro utente
    REVIEW_LIKED      // like ad una tua review
}

@OptIn(ExperimentalUuidApi::class)
data class Notification(
    val id: String = Uuid.random().toString(),
    val recipientUserId: String = "",
    val type: String = NotificationType.RECOMMENDED_RECIPE.name,
    val title: String = "",
    val message: String = "",
    val targetRecipeId: String? = null,  // per navigare al tap
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)