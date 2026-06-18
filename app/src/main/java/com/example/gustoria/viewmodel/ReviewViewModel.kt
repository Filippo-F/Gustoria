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
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.data.utils.ImageUploader
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReviewViewModel(
    private val reviewRepository: ReviewRepoInterface,
    private val recipeRepository: RecipeRepoInterface,
    private val notificationRepo: NotificationRepoInterface
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

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

    fun addReview(review: Review, onSuccess: () -> Unit) {
        if (_isSubmitting.value) return // Blocca se un invio è già in corso

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                // Upload image
                val publicPhotoUrl = review.photoUri?.let { uri ->
                    ImageUploader.uploadImage(uri, "reviews")
                } ?: review.photoUri

                // Create review
                val finalReview = review.copy(photoUri = publicPhotoUrl)
                reviewRepository.addReview(finalReview)

                // Notifications
                recipeRepository.getRecipeById(finalReview.recipeId).first()?.let { recipe ->
                    if (recipe.ownerId != (SessionManagerFacade.currentUserId.value ?: "")) {
                        notificationRepo.addNotification(
                            Notification(
                                recipientUserId = recipe.ownerId,
                                type = NotificationType.REVIEW_RECEIVED.name,
                                title = "New review on your recipe",
                                message = "Someone reviewed \"${recipe.name}\".",
                                targetRecipeId = recipe.id
                            )
                        )
                    }
                }
                // Now go back to previous screen
                onSuccess()
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    // (Updated when user login)
    @OptIn(ExperimentalCoroutinesApi::class)
    fun isLikedFlow(reviewId: String): Flow<Boolean> =
        SessionManagerFacade.currentUserId.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) flowOf(false)
            else reviewRepository.isLiked(uid, reviewId)
        }

    fun toggleLike(reviewId: String) {
        viewModelScope.launch {
            val userId = SessionManagerFacade.currentUserId.value ?: ""
            val isLiked = reviewRepository.isLiked(userId, reviewId).first()
            if (isLiked) {
                reviewRepository.removeLike(userId, reviewId)
            } else {
                reviewRepository.addLike(userId, reviewId)
                // Notify review author
                reviewRepository.getReviewById(reviewId).first()?.let { review ->
                    if (review.userId != userId) {
                        notificationRepo.addNotification(
                            Notification(
                                recipientUserId = review.userId,
                                type = NotificationType.REVIEW_LIKED.name,
                                title = "Someone liked your review!",
                                message = "Your review received a like.",
                                targetRecipeId = review.recipeId
                            )
                        )
                    }
                }
            }
        }
    }

    fun deleteReview(reviewId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Get review to find image URL
            reviewRepository.getReviewById(reviewId).first()?.let { review ->
                review.photoUri?.let { url ->
                    ImageUploader.deleteImage(url, "reviews")
                }
            }
            reviewRepository.deleteReview(reviewId)
            onSuccess()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val reviewRepository = application.container.reviewRepository
                val recipeRepository = application.container.recipeRepository
                val notificationRepository = application.container.notificationRepository
                ReviewViewModel(reviewRepository, recipeRepository, notificationRepository)
            }
        }
    }
}
