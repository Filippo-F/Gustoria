package com.example.gustoria

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.AppBottomNavBar
import com.example.gustoria.ui.NavDestination
import com.example.gustoria.ui.theme.GustoriaTheme

@Preview(name = "Portrait", showSystemUi = true)
@Composable
fun RecipeScreenPreviewPortrait() {
    GustoriaTheme(dynamicColor = false) {
        RecipeScreen(viewModel = viewModel(), onBack = {}, onNavigate = {})
    }
}

@Preview(name = "Landscape", showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
fun RecipeScreenPreviewLandscape() {
    GustoriaTheme(dynamicColor = false) {
        RecipeScreen(viewModel = viewModel(), onBack = {}, onNavigate = {})
    }
}

@Composable
fun RecipeScreen(viewModel: RecipeViewModel, onBack: () -> Unit = {}, onNavigate: (NavDestination) -> Unit) {
    val recipe = viewModel.recipe

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                AppBottomNavBar(
                    currentDestination = NavDestination.PROFILE,
                    onNavigate = onNavigate
                )
            }
        }
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
                    Image(
                        painter = painterResource(id = R.drawable.random_food),
                        contentDescription = "Recipe Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
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

                    // Back Button
                    IconButton(
                        onClick = onBack,
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

                    // Favorite Button
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (viewModel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (viewModel.isFavorite) Color.Red else Color.White
                        )
                    }

                    // Recipe Title
                    Text(
                        text = recipe.title,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 28.sp,
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

            // Recipe info
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
                    VerticalDivider(modifier = Modifier.height(32.dp), color = Color.Gray.copy(alpha = 0.4f))
                    InfoItem(icon = Icons.Default.SignalCellularAlt, text = recipe.difficulty)
                    VerticalDivider(modifier = Modifier.height(32.dp), color = Color.Gray.copy(alpha = 0.4f))
                    InfoItem(icon = Icons.Default.Schedule, text = "${recipe.cookingTimeMinutes}m")
                    VerticalDivider(modifier = Modifier.height(32.dp), color = Color.Gray.copy(alpha = 0.4f))
                    InfoItem(
                        icon = Icons.Default.Star,
                        text = "${recipe.rating} (${recipe.reviews})",
                        iconTint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

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
                            onClick = { viewModel.toggleMade() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (viewModel.isMade) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle completion",
                                tint = if (viewModel.isMade) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (viewModel.isMade) "Made it!" else "I've made this",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (viewModel.isMade) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.7f)
                        )
                    }

                    // Create Your Own Button
                    Button(
                        onClick = {  },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CREATE YOUR OWN RECIPE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            //Description
            item {
                Text(
                    recipe.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text (
                        text = "${recipe.servings} Servings",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.secondary
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
                            ingredient.quantity,
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
                    fontSize = 20.sp,
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
                            .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        step,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    text: String,
    iconTint: Color = MaterialTheme.colorScheme.secondary
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
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
