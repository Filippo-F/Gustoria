package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gustoria.data.auth.SessionManager
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
    ReviewFormScreen(
        onBack = navActions::navigateBack,
        onPostReview = { description, rating, photoUri ->
            val review = Review(
                userId = SessionManager.CURRENT_LOGGED_IN_USER_ID,
                recipeId = recipeId,
                description = description,
                rating = rating,
                photoUri = photoUri.ifBlank { null },
                timestamp = System.currentTimeMillis().toString()
            )
            viewModel.addReview(review)
            navActions.navigateBack()
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
        onWriteReview = { navActions.navigateToAddReview(recipeId) },
        onProfileClick = { userId ->
            if (userId == SessionManager.CURRENT_LOGGED_IN_USER_ID) {
                navActions.navigateToProfile()
            } else {
                navActions.navigateToOtherProfile(userId)
            }
        }
    )
}
