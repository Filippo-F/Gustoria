package com.example.gustoria

enum class CookingRole {
    FOOD_LOVER,
    HOME_COOK,
    CONTENT_CREATOR,
    PROFESSIONAL_CHEF;

    fun displayName(): String = when (this) {
        FOOD_LOVER -> "Food Lover"
        HOME_COOK -> "Home Cook"
        CONTENT_CREATOR -> "Content Creator"
        PROFESSIONAL_CHEF -> "Professional Chef"
    }
}
data class UserClass(
    //non modificabili
    val fullName: String,
    val email: String,
    val internalID: Int = 101,

    //modificabili
    val nickname: String,
    val phoneNumber: String = "",
    val description: String = "",
    val profileImageUri: String? = null, //per ora
    val cookingRole: CookingRole? = null,

    val cuisinePreferences: List<String> = emptyList(),
    val dietaryRestrictions: List<String> = emptyList(),
    val favoriteIngredients: List<String> = emptyList(),

    val numberOfRecipes: Int = 0,
    val numberOfFollowers: Int = 0,
    val numberOfLikes: Int = 0,
)
