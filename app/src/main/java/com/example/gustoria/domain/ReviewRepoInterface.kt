package com.example.gustoria.domain

import com.example.gustoria.dataclass.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepoInterface {
    // Get all review on the platform for a specific recipe
    fun getReviewsByRecipe(recipeId: String): Flow<List<Review>>

    // Get all reviews written by a specific user
    fun getReviewsByUser(userId: String): Flow<List<Review>>

    // Get a specific review by its ID
    fun getReviewById(reviewId: String): Flow<Review?>

    // Get all reviews available
    fun getAllReviews(): Flow<List<Review>>

    // Add a review to a recipe
    suspend fun addReview(review: Review)

    // Update an existing review
    suspend fun updateReview(reviewId: String, review: Review)

    // Delete a review from a recipe
    suspend fun deleteReview(reviewId: String)

    // True se userId ha già messo like a questa review
    fun isLiked(userId: String, reviewId: String): Flow<Boolean>

    // Aggiunge userId a likedByUserIds della review
    suspend fun addLike(userId: String, reviewId: String)

    // Rimuove userId da likedByUserIds della review
    suspend fun removeLike(userId: String, reviewId: String)
}