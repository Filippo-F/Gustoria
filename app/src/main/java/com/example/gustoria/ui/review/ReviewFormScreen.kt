package com.example.gustoria.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import com.example.gustoria.ui.ThreeItemTopNavbar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gustoria.dataclass.Review
import com.example.gustoria.SessionManager
import com.example.gustoria.viewmodel.ReviewViewModel
import com.example.gustoria.domain.ReviewRepoInterface

class ReviewFormActions(private val navController: NavHostController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewFormScreen(
    recipeId: String,
    navController: NavHostController,
    reviewRepository: ReviewRepoInterface,
    viewModel: ReviewViewModel = viewModel(factory = ReviewViewModel.provideFactory(reviewRepository))
) {
    var rating by remember { mutableStateOf(5f) }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf("") }

    val actions = remember(navController) { ReviewFormActions(navController) }
    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = "Write a review",
                onBack = actions.navigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Rating", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    IconButton(onClick = { rating = (index + 1).toFloat() }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.3f
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri.isNotBlank()) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Review image preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "No photo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = photoUri,
                onValueChange = { photoUri = it },
                label = { Text("Image URL or asset path") },
                placeholder = { Text("file:///android_asset/pasta.jpg") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Write your review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val review = Review(
                        userId = SessionManager.CURRENT_LOGGED_IN_USER_ID,
                        recipeId = recipeId,
                        description = description,
                        rating = rating,
                        photoUri = photoUri.ifBlank { null },
                        timestamp = System.currentTimeMillis().toString()
                    )
                    viewModel.addReview(review)
                    actions.navigateBack()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("POST")
            }
        }
    }
}

