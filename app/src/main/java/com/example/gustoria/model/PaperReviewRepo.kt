package com.example.gustoria.model

import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.ReviewRepoInterface
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaperReviewRepo: ReviewRepoInterface {
    private val _placeholderReviews = listOf(
        Review(
            userId = "101",
            recipeId = "",
            description = "Delicious and easy to follow; loved it!",
            rating = 5.0f,
            likes = 3,
            photoUri = null,
            timestamp = ""
        ),
        Review(
            userId = "202",
            recipeId = "",
            description = "Nice flavors, I reduced the salt and it worked well.",
            rating = 4.0f,
            likes = 1,
            photoUri = null,
            timestamp = ""
        )
    )

    private val reviewBook = Paper.book("reviews")

    // In memory state
    private val _reviews = MutableStateFlow<List<Review>>(loadInitialReviews())

    private fun loadInitialReviews(): List<Review> {
        return try {
            reviewBook.allKeys.mapNotNull { key ->
                reviewBook.read<Review>(key)
            }
        } catch (e: Exception) {
            reviewBook.destroy()
            emptyList()
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // First time load
    init {
        scope.launch {
            if (reviewBook.allKeys.isEmpty()) {
                val recipeKeys = Paper.book("recipes").allKeys
                val prepared = _placeholderReviews.mapIndexed { idx, r ->
                    val recipeId = recipeKeys.getOrNull(idx) ?: ""
                    r.copy(recipeId = recipeId, timestamp = System.currentTimeMillis().toString())
                }
                prepared.forEach { reviewBook.write(it.id, it) }
                _reviews.update { prepared }
            }
        }
    }

    override fun getReviewsByRecipe(recipeId: String): Flow<List<Review>> =
        _reviews
            .map { reviews ->
                reviews.filter { it.recipeId == recipeId }
            }
            .flowOn(Dispatchers.IO)

    override fun getReviewsByUser(userId: String): Flow<List<Review>> =
        _reviews
            .map { reviews ->
                reviews.filter { it.userId == userId }
            }
            .flowOn(Dispatchers.IO)

    override fun getReviewById(reviewId: String): Flow<Review?> =
        _reviews
            .map { list ->
                list.find { it.id == reviewId }
            }
            .flowOn(Dispatchers.IO)

    override fun getAllReviews(): Flow<List<Review>> = _reviews.asStateFlow()

    override suspend fun addReview(review: Review) = withContext(Dispatchers.IO) {
        reviewBook.write<Review>(review.id, review)
        _reviews.update { list ->
            list + review
        }
    }

    override suspend fun updateReview(
        reviewId: String,
        review: Review
    ) = withContext(Dispatchers.IO) {
        reviewBook.write<Review>(review.id, review)
        _reviews.update { list ->
            list.map { if (it.id == reviewId) review else it }
        }
    }

    override suspend fun deleteReview(reviewId: String) = withContext(Dispatchers.IO) {
        reviewBook.delete(reviewId)
        _reviews.update { list ->
            list.filter { it.id != reviewId }
        }
    }
}