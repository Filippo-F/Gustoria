package com.example.gustoria.Dataclass

import java.util.Optional

data class Review (
    val authorName: String,
    val authorNickname: String,
    val description: String,
    val rating: Float,
    val likes: Int,
    val photoUri: Optional<String>,
    val timestamp: String,
)
