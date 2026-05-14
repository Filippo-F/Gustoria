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
    val bio: String = "",
    val cookingRole: CookingRole = CookingRole.NONE,

    val name: String = "",
    val username: String = "",

    val email: String = "",
    val phoneNumber: String = "",

    val profileImageUrl: String? = null,

    val cuisinePreferences: List<String> = emptyList(),
    val dietaryRestrictions: List<String> = emptyList(),
    val favoriteIngredients: List<String> = emptyList(),

    val numberOfRecipes: Int = 0,
    val numberOfFollowers: Int = 0,
    val numberOfLikes: Int = 0,

    // Reference ids
    val savedRecipesIds: List<String> = emptyList(),
    val triedRecipesIds: List<String> = emptyList(),
    val followingIds: List<String> = emptyList(),

    val createdAt: String = ""
)
