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
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ReviewViewModel(
    private val reviewRepository: ReviewRepoInterface,
    private val recipeRepository: RecipeRepoInterface,
    private val notificationRepo: NotificationRepoInterface
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

    fun addReview(review: Review, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Upload image
            val publicPhotoUrl = review.photoUri?.let { uri ->
                com.example.gustoria.data.utils.ImageUploader.uploadImage(uri, "reviews")
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
        }
    }

    fun isLikedFlow(reviewId: String): Flow<Boolean> =
        reviewRepository.isLiked(SessionManagerFacade.currentUserId.value ?: "", reviewId)

    fun toggleLike(reviewId: String) {
        viewModelScope.launch {
            val userId = SessionManagerFacade.currentUserId.value ?: ""
            val isLiked = reviewRepository.isLiked(userId, reviewId).first()
            if (isLiked) reviewRepository.removeLike(userId, reviewId)
            else reviewRepository.addLike(userId, reviewId)
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
