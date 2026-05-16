package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.ReviewRepoInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepoInterface
) : ViewModel() {

    fun reviewsForRecipe(recipeId: String): StateFlow<List<Review>> =
        reviewRepository.getReviewsByRecipe(recipeId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun reviewsForUser(userId: String): StateFlow<List<Review>> =
        reviewRepository.getReviewsByUser(userId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.addReview(review)
        }
    }

    companion object {
        fun provideFactory(
            reviewRepository: ReviewRepoInterface
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ReviewViewModel(reviewRepository) as T
            }
    }
}
