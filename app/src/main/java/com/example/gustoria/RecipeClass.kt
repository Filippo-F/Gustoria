package com.example.gustoria

data class RecipeProposal (
    val title: String,
    val image: Int,
    val cost: String,       // es. "€", "€€"
    val difficulty: String,          // es. "Medium", "High"
    val cookingTimeMinutes: Int,
    val servings: Int,
    val ingredients: List<Ingredient>,
    val steps: List<String>
)

data class Ingredient(
    val name: String,
    val quantity: String             // es. "200g"
)