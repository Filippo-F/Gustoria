package com.example.gustoria.dataclass

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class CookingRole {
    NONE,
    FOOD_LOVER,
    HOME_COOK,
    CONTENT_CREATOR,
    PROFESSIONAL_CHEF;
}

@OptIn(ExperimentalUuidApi::class)
data class User(
    val internalId: String = Uuid.random().toString(),

    val nickname: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val description: String = "",
    val cookingRole: CookingRole = CookingRole.NONE,

    val phoneNumber: String = "",

    val profileImageUri: String? = null,

    val cuisinePreferences: List<String> = emptyList(),
    val dietaryRestrictions: List<String> = emptyList(),
    val favouriteMealTypes: List<String> = emptyList(),

    val numberOfFollowers: Int = 0,

    // Reference ids
    val favouriteRecipesIds: List<String> = emptyList(),
    val triedRecipesIds: List<String> = emptyList(),
    val followingIds: List<String> = emptyList(),

    val pushNotificationsEnabled: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)
