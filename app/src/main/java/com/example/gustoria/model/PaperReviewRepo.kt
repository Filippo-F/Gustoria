package com.example.gustoria.model

import com.example.gustoria.Dataclass.Review
import com.example.gustoria.domain.ReviewRepoInterface
import kotlinx.coroutines.flow.Flow

class PaperReviewRepo: ReviewRepoInterface {
    override fun getReviewsByRecipe(recipeId: String): Flow<List<Review>> {
        TODO("Not yet implemented")
    }

    override fun getReviewsByUser(userId: String): Flow<List<Review>> {
        TODO("Not yet implemented")
    }

    override fun getReviewById(reviewId: String): Flow<Review?> {
        TODO("Not yet implemented")
    }

    override suspend fun addReview(review: Review) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReview(
        reviewId: String,
        review: Review
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReview(reviewId: String) {
        TODO("Not yet implemented")
    }
}