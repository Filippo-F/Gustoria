package com.example.gustoria.ui.recipe.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.R

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {
        RecipeCardContent(recipe)
    }
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}

@Composable
fun RecipeCardContent(recipe: Recipe) {
    AsyncImage(
        model = recipe.imageUri,
        contentDescription = "Image of ${recipe.name}",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
        fallback = painterResource(id = R.drawable.no_image),
        error = painterResource(id = R.drawable.no_image)
    )

    Spacer(Modifier.height(8.dp))

    Text(
        text = recipe.name.ifBlank { "Untitled recipe" },
        style = MaterialTheme.typography.titleMedium,
        maxLines = 2
    )

    Spacer(Modifier.height(4.dp))

    Text(
        text = "${recipe.cost} \u2022 ${recipe.difficulty}",
        style = MaterialTheme.typography.bodySmall
    )

    Text(
        text = "${recipe.cookingTimeMinutes} min \u00b7 ${recipe.servings} serv.",
        style = MaterialTheme.typography.bodySmall
    )
}
