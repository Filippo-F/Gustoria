package com.example.gustoria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import com.example.gustoria.ui.AppBottomNavBar
import com.example.gustoria.ui.NavDestination
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Preview(name = "Portrait", showSystemUi = true)
@Composable
fun RecipeScreenPreviewPortrait() {
    MaterialTheme {
        RecipeScreen(viewModel = viewModel(), onBack = {}, onNavigate = {})
    }
}

@Composable
fun RecipeScreen(viewModel: RecipeViewModel, onBack: () -> Unit = {}, onNavigate: (NavDestination) -> Unit) {
    val recipe = viewModel.recipe

    Scaffold(
        bottomBar = {
            AppBottomNavBar(
                currentDestination = NavDestination.EXPLORE,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            // TOP BAR with back arrow
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(recipe.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // HERO IMAGE
            item {
                Image(
                    painter = painterResource(id = recipe.image),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.Crop, // Crops the image to perfectly fill the box
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp) // Gives it a nice, large "Hero" presence
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp)) // Rounds the corners to match tags
                )
            }

            // INFO (cost, difficulty, cooking time, servings)
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Added to give breathing room if they wrap!
                ) {
                    RecipeTag("💰 ${recipe.cost}")
                    RecipeTag("📊 ${recipe.difficulty}")
                    RecipeTag("⏱ ${recipe.cookingTimeMinutes} min")
                    RecipeTag("🍽 ${recipe.servings} porzioni")
                }
            }

            // Ingredients
            item {
                Text(
                    "Ingredienti",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            items(recipe.ingredients) { ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(ingredient.name)
                    Text(ingredient.quantity, color = MaterialTheme.colorScheme.onSurface)
                }
                HorizontalDivider()
            }

            item {
                Text(
                    "Preparazione",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            itemsIndexed(recipe.steps) { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(step, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun RecipeTag(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp)
    }
}
