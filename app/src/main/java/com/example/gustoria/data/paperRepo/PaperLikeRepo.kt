package com.example.gustoria.data.paperRepo

import com.example.gustoria.dataclass.LikeTarget
import com.example.gustoria.domain.LikeRepoInterface
import kotlinx.coroutines.flow.Flow

class PaperLikeRepo: LikeRepoInterface {
    override suspend fun like(
        userId: String,
        targetId: String,
        targetType: LikeTarget
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun unlike(userId: String, targetId: String) {
        TODO("Not yet implemented")
    }

    override fun isLiked(
        userId: String,
        targetId: String
    ): Flow<Boolean> {
        TODO("Not yet implemented")
    }
}