package com.example.gustoria.Dataclass

data class Ingredient(
    val name: String = "",
    val quantity: Int = 0,
    val unit: String = "g",
    val kcalPer100g: Int = 0
)
