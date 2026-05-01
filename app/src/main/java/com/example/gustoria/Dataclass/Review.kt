package com.example.gustoria.Dataclass

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Review (
    val id: String = Uuid.random().toString(),
    val authorId: String = "",
    val recipeId: String = "",
    val description: String = "",
    val rating: Float = 0f,
    val likes: Int = 0,
    val photoUri: String? = null,
    val timestamp: String = "",
)
