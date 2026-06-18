package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.dataclass.Review
import com.example.gustoria.ui.review.ReviewFormScreen
import com.example.gustoria.ui.review.ReviewsListScreen
import com.example.gustoria.viewmodel.ReviewViewModel
import kotlinx.serialization.Serializable

@Serializable
object Review {
    @Serializable
    data class AddReview(val recipeId: String)
    @Serializable
    data class ReviewsList(val recipeId: String)
}

@Composable
fun AddReviewDestination(
    recipeId: String,
    navActions: GustoriaNavigationActions,
    viewModel: ReviewViewModel
) {
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

    ReviewFormScreen(
        isSubmitting = isSubmitting,
        onBack = navActions::navigateBack,
        onPostReview = { description, rating, photoUri ->
            val review = Review(
                userId = SessionManagerFacade.currentUserId.value ?: "",
                recipeId = recipeId,
                description = description,
                rating = rating,
                photoUri = photoUri.ifBlank { null },
                timestamp = System.currentTimeMillis()
            )
            // Pass navActions.navigateBack() so that it will be called
            // ONLY when ViewModel finished to upload photo
            viewModel.addReview(review, onSuccess = { navActions.navigateBack() })
        }
    )
}

@Composable
fun ReviewsListDestination(
    recipeId: String,
    navActions: GustoriaNavigationActions,
    viewModel: ReviewViewModel
) {
    val reviews by remember(recipeId) { viewModel.reviewsForRecipe(recipeId) }.collectAsStateWithLifecycle(initialValue = null)

    ReviewsListScreen(
        reviews = reviews,
        onBack = navActions::navigateBack,
        onWriteReview = {
            if (SessionManagerFacade.isLoggedIn) {
                navActions.navigateToAddReview(recipeId)
            } else {
                navActions.navigateToActionRequirement("write a review")
            }
        },
        onProfileClick = { userId ->
            if (userId == (SessionManagerFacade.currentUserId.value ?: "")) {
                navActions.navigateToProfile()
            } else {
                navActions.navigateToOtherProfile(userId)
            }
        },
        isLikedFlow = { reviewId -> viewModel.isLikedFlow(reviewId) },
        onToggleLike = { reviewId ->
            if (SessionManagerFacade.isLoggedIn) {
                viewModel.toggleLike(reviewId)
            } else {
                navActions.navigateToActionRequirement("like this review")
            }
        }
    )
}
