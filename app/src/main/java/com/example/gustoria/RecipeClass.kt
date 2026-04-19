package com.example.gustoria

data class RecipeProposal (
    val title: String,
    val imageUri: String? = null,
    val cost: String,       // es. "€", "€€"
    val difficulty: String,          // es. "Medium", "High"
    val cookingTimeMinutes: Int,
    val servings: Int,
    val rating: Float,
    val reviews: Int,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val description: String
)

data class Ingredient(
    val name: String,
    val quantity: String             // es. "200g"
)