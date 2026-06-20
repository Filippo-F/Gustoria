package com.example.gustoria.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gustoria.R
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.ui.recipe.components.RecipeCard
import com.example.gustoria.ui.recipe.components.RecipeCardContent
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview

@MultiPreview
@Preview
@Composable
fun HomeScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        HomeScreen(
            recommendedRecipes = emptyList(),
            otherRecipes = emptyList(),
            unreadCount = 3,
            profileImageUri = null,
            userInitials = "AB",
            onNavigateToProfile = {},
            onNavigateToNotifications = {},
            onNavigateToRecipeDetails = {},
            onCategorySelected = {}
        )
    }
}

@Composable
fun HomeScreen(
    recommendedRecipes: List<Recipe>,
    otherRecipes: List<Recipe>,
    unreadCount: Int,
    profileImageUri: String?,
    userInitials: String?,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToRecipeDetails: (String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // variante TopNavbar
        item {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                titleContent = {
                    Text(
                        text = "GUSTORIA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                },
                leadingContent = {
                    IconButton(onClick = onNavigateToProfile) {
                        ProfileAvatar(
                            profileImageUri = profileImageUri,
                            initials = userInitials,
                            size = 32
                        )
                    }
                },
                trailingContent = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge {
                                        Text(
                                            text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
        }

        // Section 1: "Recommended For You"
        item {
            HomeSectionHeader(title = "RECOMMENDED FOR YOU")
            Spacer(Modifier.height(8.dp))
            if (recommendedRecipes.isEmpty()) {
                Text(
                    text = "No recommendations available yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                RecommendedRow(
                    recipes = recommendedRecipes,
                    onRecipeClick = onNavigateToRecipeDetails
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // Section 2: "Explore Categories"
        item {
            HomeSectionHeader(title = "EXPLORE CATEGORIES")
            Spacer(Modifier.height(8.dp))
            val categoryTitles = stringArrayResource(R.array.recipe_meal_types).toList()
            CategoryChipsRow(
                categories = categoryTitles,
                onCategorySelected = onCategorySelected
            )
            Spacer(Modifier.height(16.dp))
        }

        // Section 3: "Recent Creations"
        item {
            HomeSectionHeader(title = "RECENT CREATIONS")
            Spacer(Modifier.height(8.dp))
        }

        recentCreationsGrid(
            recipes = otherRecipes,
            onRecipeClick = onNavigateToRecipeDetails
        )

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun CategoryChipsRow(
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = false,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
private fun RecommendedRow(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
                modifier = Modifier.width(260.dp),
                content = {
                    RecipeCardContent(
                        recipe = recipe,
                        imageHeight = 160.dp
                    )
                }
            )
        }
    }
}

private fun LazyListScope.recentCreationsGrid(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit
) {
    if (recipes.isEmpty()) {
        item {
            Text(
                text = "No recipes created yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    } else {
        val latestRecipes = recipes.take(10)
        val chunkedRecipes = latestRecipes.chunked(2)
        items(chunkedRecipes) { rowRecipes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowRecipes.forEach { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill space if the row has only one item
                if (rowRecipes.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    profileImageUri: String?,
    initials: String? = null,
    size: Int = 32,
    modifier: Modifier = Modifier
) {
    val sizeDp = size.dp
    when {
        profileImageUri != null -> {
            AsyncImage(
                model = profileImageUri,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(sizeDp)
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clip(CircleShape)
            )
        }
        !initials.isNullOrBlank() -> {
            Box(
                modifier = modifier
                    .size(sizeDp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else -> {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = modifier.size(sizeDp)
            )
        }
    }
}
