package com.example.gustoria.domain

import com.example.gustoria.dataclass.LikeTarget
import kotlinx.coroutines.flow.Flow

interface LikeRepoInterface {
    // Like a recipe/review
    suspend fun like (userId: String, targetId: String, targetType: LikeTarget)

    // Unlike a recipe/review
    suspend fun unlike (userId: String, targetId: String)

    // Check if a user has liked a recipe/review
    fun isLiked(userId: String, targetId: String): Flow<Boolean>
}