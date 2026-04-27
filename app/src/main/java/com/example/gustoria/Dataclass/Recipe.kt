package com.example.gustoria.Dataclass

data class Recipe(
    val name: String,
    val imageUri: String? = null,
    val cost: String, // es. "€", "€€"
    val difficulty: String, // es. "Medium", "High"
    val cookingTimeMinutes: Int,
    val servings: Int,
    val rating: Float,
    val reviews: List<Review>,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val description: String
)
