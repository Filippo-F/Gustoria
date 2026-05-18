package com.example.gustoria.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.gustoria.ui.ThreeItemTopNavbar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gustoria.AddReview
import com.example.gustoria.OtherProfile
import com.example.gustoria.Profile
import com.example.gustoria.SessionManager
import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.viewmodel.ReviewViewModel

class ReviewsListActions(private val navController: NavHostController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val navigateToAddReview: (String) -> Unit = { recipeId ->
        navController.navigate(AddReview(recipeId))
    }

    val navigateToProfile: (String) -> Unit = { userId ->
        navController.navigate(OtherProfile(userId))
    }

    val navigateToOwnedProfile: () -> Unit = {
        navController.navigate(Profile)
    }
}

@Composable
fun ReviewsListScreen(
    recipeId: String,
    navController: NavHostController,
    reviewRepository: ReviewRepoInterface,
    userRepository: UserRepoInterface,
    viewModel: ReviewViewModel = viewModel(factory = ReviewViewModel.provideFactory(reviewRepository))
) {
    val actions = remember(navController) { ReviewsListActions(navController) }
    val reviews by viewModel.reviewsForRecipe(recipeId).collectAsStateWithLifecycle()

    val average = remember(reviews) {
        if (reviews.isEmpty()) 0f else reviews.map { it.rating }.average().toFloat()
    }

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = "Reviews",
                onBack = actions.navigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "COMMUNITY PULSE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = String.format(java.util.Locale.ROOT, "%.1f", average),
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Based on ${reviews.size} review${if (reviews.size == 1) "" else "s"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            actions.navigateToAddReview(recipeId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Write a Review")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "No reviews yet. Be the first to share your experience!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(reviews, key = { it.id }) { review ->
                        ReviewRow(review = review, userRepository = userRepository, onProfileClick = { userId ->
                            if (userId.isNotBlank()) {
                                if (userId == SessionManager.CURRENT_LOGGED_IN_USER_ID) {
                                    actions.navigateToOwnedProfile()
                                } else {
                                    actions.navigateToProfile(userId)
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewRow(review: Review, userRepository: UserRepoInterface, onProfileClick: (String) -> Unit) {
    val user by userRepository.getUserById(review.userId).collectAsStateWithLifecycle(initialValue = null)
    val displayName = if (review.userId == SessionManager.CURRENT_LOGGED_IN_USER_ID) {
        "You"
    } else {
        user?.fullName ?: review.userId
    }
    val initials = if (displayName == "You") {
        "Y"
    } else {
        displayName.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase()
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(enabled = review.userId.isNotBlank()) {
                    onProfileClick(review.userId)
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.rating.coerceIn(0f, 5f).toInt()) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = review.description, style = MaterialTheme.typography.bodyMedium)

            if (!review.photoUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AsyncImage(
                        model = review.photoUri,
                        contentDescription = "Review photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
