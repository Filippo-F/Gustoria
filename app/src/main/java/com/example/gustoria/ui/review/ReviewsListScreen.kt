package com.example.gustoria.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.gustoria.ui.ThreeItemTopNavbar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.viewmodel.ReviewViewModel

class ReviewsListActions(private val navController: NavHostController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}

@Composable
fun ReviewsListScreen(
    recipeId: String,
    navController: NavHostController,
    reviewRepository: ReviewRepoInterface,
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
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            Text(text = "Average: ${String.format("%.1f", average)}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(reviews) { review ->
                    ReviewRow(review)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun ReviewRow(review: Review) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "User: ${review.userId}", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${review.rating}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = review.description, style = MaterialTheme.typography.bodyMedium)
    }
}
