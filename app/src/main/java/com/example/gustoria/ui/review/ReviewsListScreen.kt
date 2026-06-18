package com.example.gustoria.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.dataclass.Review
import com.example.gustoria.ui.review.components.ReviewCard

@Composable
fun ReviewsListScreen(
    reviews: List<Review>?,
    onBack: () -> Unit,
    onWriteReview: () -> Unit,
    onProfileClick: (String) -> Unit,
    isLikedFlow: ((String) -> Flow<Boolean>)? = null,
    onToggleLike: ((String) -> Unit)? = null
) {
    val average = remember(reviews) {
        if (reviews.isNullOrEmpty()) 0f else reviews.map { it.rating }.average().toFloat()
    }

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = "Reviews",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        if (reviews != null) {
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
                                            tint = MaterialTheme.colorScheme.tertiary,
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
                            onClick = onWriteReview,
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
                            val isLiked by remember(review.id) {
                                isLikedFlow?.invoke(review.id) ?: kotlinx.coroutines.flow.flowOf(false)
                            }.collectAsStateWithLifecycle(initialValue = false)
                            ReviewCard(
                                review = review,
                                onProfileClick = onProfileClick,
                                isLiked = isLiked,
                                onToggleLike = onToggleLike
                            )
                        }
                    }
                }
            }
        }
    }
}
