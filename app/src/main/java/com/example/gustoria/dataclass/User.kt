package com.example.gustoria.dataclass

import java.time.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class CookingRole {
    NONE,
    FOOD_LOVER,
    HOME_COOK,
    CONTENT_CREATOR,
    PROFESSIONAL_CHEF;

    fun displayName(): String = when (this) {
        NONE -> "None"
        FOOD_LOVER -> "Food Lover"
        HOME_COOK -> "Home Cook"
        CONTENT_CREATOR -> "Content Creator"
        PROFESSIONAL_CHEF -> "Professional Chef"
    }
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
    val favoriteIngredients: List<String> = emptyList(),

    val numberOfRecipes: Int = 0,
    val numberOfFollowers: Int = 0,

    // Reference ids
    val favouriteRecipesIds: List<String> = emptyList(),
    val triedRecipesIds: List<String> = emptyList(),
    val followingIds: List<String> = emptyList(),

    val createdAt: String = LocalDateTime.now().toString()
)
