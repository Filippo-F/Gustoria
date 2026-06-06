package com.example.gustoria.ui.recipe

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.viewmodel.RecipeViewModel
import com.example.gustoria.viewmodel.ReviewViewModel
import com.example.gustoria.ui.navigation.GustoriaNavigationActions
import coil.compose.AsyncImage
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.dataclass.Review
import com.example.gustoria.R
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.ui.review.components.ReviewCard

@Composable
fun RecipeDetailsScreen(
    recipeId: String,
    navActions: GustoriaNavigationActions,
    onBack: (() -> Unit)? = null,
    recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory),
    reviewViewModel: ReviewViewModel = viewModel(factory = ReviewViewModel.Factory)
) {
    val isFavourite by remember(recipeId) { recipeViewModel.isFavouriteFlow(recipeId) }.collectAsStateWithLifecycle(initialValue = false)
    val isTried by remember(recipeId) { recipeViewModel.isTriedFlow(recipeId) }.collectAsStateWithLifecycle(initialValue = false)
    val recipe by recipeViewModel.selectedRecipe.collectAsStateWithLifecycle()
    val reviews by remember(recipeId) { reviewViewModel.reviewsForRecipe(recipeId) }.collectAsStateWithLifecycle(initialValue = null)

    val backAction = onBack ?: navActions::navigateBack

    LaunchedEffect(recipeId) {
        recipeViewModel.selectRecipe(recipeId)
    }

    recipe?.let { r ->
        RecipeDetailsContent(
            recipe = r,
            reviews = reviews,
            isOwner = recipeViewModel.isOwnedByCurrentUser(r),
            isFavourite = isFavourite,
            onToggleFavourite = {
                if (SessionManagerFacade.isLoggedIn) {
                    recipeViewModel.toggleFavourite(r.id)
                } else {
                    navActions.navigateToActionRequirement("add this recipe to your favorites")
                }
            },
            isTried = isTried,
            onToggleTried = {
                if (SessionManagerFacade.isLoggedIn) {
                    recipeViewModel.toggleTried(r.id)
                } else {
                    navActions.navigateToActionRequirement("mark this recipe as cooked")
                }
            },
            onBackClick = backAction,
            onWriteReview = {
                if (SessionManagerFacade.isLoggedIn) {
                    navActions.navigateToAddReview(r.id)
                } else {
                    navActions.navigateToActionRequirement("write a review")
                }
            },
            onViewReviews = {
                if (SessionManagerFacade.isLoggedIn) {
                    navActions.navigateToReviewsList(r.id)
                } else {
                    navActions.navigateToActionRequirement("view all reviews")
                }
            },
            onProfileClick = { userId ->
                if (userId.isNotBlank()) {
                    if (userId == (SessionManagerFacade.currentUserId.value ?: "")) {
                        navActions.navigateToProfile()
                    } else {
                        navActions.navigateToOtherProfile(userId)
                    }
                }
            },
            onDeleteClick = {
                recipeViewModel.deleteRecipe(r.id)
                backAction()
            },
            onDuplicateClick = {
                if (SessionManagerFacade.isLoggedIn) {
                    recipeViewModel.duplicateRecipe(r) { newId ->
                        navActions.navigateToEditRecipe(newId)
                    }
                } else {
                    navActions.navigateToActionRequirement("duplicate this recipe")
                }
            },
            onEditClick = { navActions.navigateToEditRecipe(r.id) }
        )
    }
}

@Composable
fun RecipeDetailsContent(
    recipe: Recipe,
    reviews: List<Review>?,
    isOwner: Boolean,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    isTried: Boolean,
    onToggleTried: () -> Unit,
    onBackClick: () -> Unit,
    onWriteReview: () -> Unit,
    onViewReviews: () -> Unit,
    onProfileClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onEditClick: () -> Unit
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showDuplicateDialog by rememberSaveable { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val avgRating = remember(reviews) { if (reviews.isNullOrEmpty()) 0f else reviews.map { it.rating }.average().toFloat() }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Food image section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    // Full Width Image
                    AsyncImage(
                        model = recipe.imageUri ?: R.drawable.no_image,
                        contentDescription = "Recipe Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        fallback = painterResource(R.drawable.no_image),
                        error = painterResource(R.drawable.no_image)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 500f
                                )
                            )
                    )

                    // "Back" Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Top-end actions: heart (favourites button) + options menu
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Heart (favourite) button
                        IconButton(
                            onClick = onToggleFavourite,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                                tint = if (isFavourite) MaterialTheme.colorScheme.error else Color.White
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        // Options (MoreVert) button + its DropdownMenu
                        Box {
                            IconButton(
                                onClick = { showTopMenu = true },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = showTopMenu,
                                onDismissRequest = { showTopMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Duplicate Recipe") },
                                    onClick = {
                                        showTopMenu = false
                                        showDuplicateDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                                )
                                if (isOwner) {
                                    DropdownMenuItem(
                                        text = { Text("Edit Recipe") },
                                        onClick = {
                                            showTopMenu = false
                                            onEditClick()
                                        },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete Recipe", color = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            showTopMenu = false
                                            showDeleteDialog = true
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Recipe Title
                    Text(
                        text = recipe.name.ifBlank { "Untitled Recipe" },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black,
                                blurRadius = 8f
                            )
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Recipe info row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoItem(icon = Icons.Default.Payments, text = recipe.cost)
                    VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    InfoItem(icon = Icons.Default.SignalCellularAlt, text = recipe.difficulty)
                    VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    InfoItem(icon = Icons.Default.Schedule, text = "${recipe.cookingTimeMinutes}m")
                    VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    InfoItem(
                        icon = Icons.Default.Star,
                        text = "${String.format("%.1f", avgRating)} (${reviews?.size})",
                        iconTint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Control row (Mark as Cooked & Duplicate button)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        IconButton(
                            onClick = onToggleTried, // Changed from { isMade = !isMade }
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isTried) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle completion",
                                tint = if (isTried) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isTried) "Cooked!" else "Mark as cooked?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTried) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant                        )
                    }

                    Button(
                        onClick = { showDuplicateDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DUPLICATE RECIPE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Description
            if (recipe.description.isNotBlank()) {
                item {
                    Text(
                        recipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Ingredients
            item {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Ingredients",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text (
                        text = "${recipe.servings} Servings",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(recipe.ingredients) { ingredient ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ingredient.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${ingredient.quantity} ${ingredient.unit}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            // Preparation
            item {
                Text(
                    "Preparation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            itemsIndexed(recipe.steps) { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        step,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Community reviews section
            if (reviews != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Surface(
                            tonalElevation = 4.dp,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Community Reviews",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = String.format("%.1f", avgRating),
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = onWriteReview,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("WRITE A REVIEW")
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = onViewReviews,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Text("VIEW ALL REVIEWS")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        reviews.take(2).forEach { review ->
                            ReviewCard(
                                review = review,
                                onProfileClick = onProfileClick,
                                showPhoto = false
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(32.dp))
                }
            }

        }

        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                recipeName = recipe.name,
                onConfirm = {
                    showDeleteDialog = false
                    onDeleteClick()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
        if (showDuplicateDialog) {
            DuplicateConfirmationDialog(
                recipeName = recipe.name,
                onConfirm = {
                    showDuplicateDialog = false
                    onDuplicateClick()
                    Toast.makeText(context, "Recipe copied to My Recipes!", Toast.LENGTH_SHORT).show()
                },
                onDismiss = {
                    showDuplicateDialog = false
                }
            )
        }
    }
}


@Composable
fun InfoItem(
    icon: ImageVector,
    text: String,
    iconTint: Color = MaterialTheme.colorScheme.tertiary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    recipeName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete recipe") },
        text = {
            Text(
                text = if (recipeName.isBlank())
                    "Are you sure you want to delete this recipe? This action cannot be undone."
                else
                    "Are you sure you want to delete \"$recipeName\"? This action cannot be undone.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DuplicateConfirmationDialog(
    recipeName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Duplicate recipe") },
        text = {
            Text(
                text = if (recipeName.isBlank())
                    "Do you want to add a copy of this recipe to your list?"
                else
                    "Do you want to add a copy of \"$recipeName\" to your list?"
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Duplicate", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
