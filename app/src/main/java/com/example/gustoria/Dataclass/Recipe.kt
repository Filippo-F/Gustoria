package com.example.gustoria.Dataclass

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Recipe(
    val id: String = Uuid.random().toString(),
    val ownerId: String = Uuid.random().toString(),
    val name: String = "",
    val imageUri: String? = null,
    val cost: String = "Undefined", // es. "€", "€€"
    val difficulty: String = "Undefined", // es. "Medium", "High"
    val cookingTimeMinutes: Int = 0,
    val servings: Int = 1,
    val rating: Float = 0f,
    val reviews: List<Review> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList(),
    val description: String = ""
)
