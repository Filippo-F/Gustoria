package com.example.gustoria.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gustoria.R

import com.example.gustoria.viewmodel.RecentSearch
import com.example.gustoria.viewmodel.TrendingCategory

@Composable
fun FeaturedSearchScreen(
    recentSearches: List<RecentSearch>,
    trendingSearches: List<String>,
    trendingCategories: List<TrendingCategory>,
    onSearchClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onTrendingTagClick: (String) -> Unit,
    onClearAllRecentSearches: () -> Unit,
    onRemoveRecentSearch: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ThreeItemTopNavbar(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            title = "GUSTORIA",
            showBackButton = false
        )

        // Mock search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(50.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(25.dp))
                .clickable { onSearchClick() },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search recipes, chefs, or tables...", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // TRENDING SEARCHES
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TRENDING SEARCHES", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.FilterList, contentDescription = "Filter", modifier = Modifier.size(16.dp), tint =MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val chunkedTags = trendingSearches.chunked(2)
                    chunkedTags.forEach { rowTags ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowTags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .clickable { onTrendingTagClick(tag) }
                                ) {
                                    Text(text = tag, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            // RECENT SEARCHES
            if (recentSearches.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("RECENT SEARCHES", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onClearAllRecentSearches, contentPadding = PaddingValues(0.dp)) {
                            Text("CLEAR ALL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(recentSearches, key = { it.id }) { recent ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onRecentSearchClick(recent.title) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = recent.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = recent.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = { onRemoveRecentSearch(recent.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // TRENDING CATEGORIES
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("TRENDING CATEGORIES", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(trendingCategories) { category ->
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onCategoryClick(category.title) }
                        ) {
                            AsyncImage(
                                model = category.imageUrl,
                                contentDescription = category.title,
                                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop,
                                fallback = painterResource(R.drawable.no_image)
                            )
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

                            Text(
                                text = category.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
