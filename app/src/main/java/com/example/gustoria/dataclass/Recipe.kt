package com.example.gustoria.dataclass

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Recipe(
    val id: String = Uuid.random().toString(),
    val ownerId: String = "",

    val name: String = "",
    val description: String = "",

    val imageUri: String? = null,

    val cost: String = "", // e.g. "€", "€€", "€€€"
    val difficulty: String = "", // e.g. "Easy", "Medium", "Hard"
    val cuisineType: String = "", // e.g. "Italian", "Japanese"  (single value)
    val mealType: String = "", // e.g. "Breakfast", "Dinner", "Dessert"

    val cookingTimeMinutes: Int = 0,
    val servings: Int = 1,
    val rating: Float = 0f, // average computed from reviews

    // Dietary tags
    val dietaryTags: List<String> = emptyList(),   // e.g. ["Vegan", "Gluten-Free"]
    // Free-form additional tags
    val tags: List<String> = emptyList(),

    val ingredients: List<RecipeIngredient> = emptyList(),
    val steps: List<String> = emptyList(),

    // IDs of users who have added this recipe to favorites (likes)
    val likedByUserIds: List<String> = emptyList(),

    // Timestamp string, set at creation, never modified
    val createdAt: String = "",
)
