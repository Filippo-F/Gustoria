package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.ReviewRepoInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepoInterface
) : ViewModel() {

    fun reviewsForRecipe(recipeId: String): StateFlow<List<Review>?> =
        reviewRepository.getReviewsByRecipe(recipeId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun reviewsForUser(userId: String): StateFlow<List<Review>?> =
        reviewRepository.getReviewsByUser(userId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun addReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.addReview(review)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val reviewRepository = application.container.reviewRepository
                ReviewViewModel(reviewRepository)
            }
        }
    }
}
