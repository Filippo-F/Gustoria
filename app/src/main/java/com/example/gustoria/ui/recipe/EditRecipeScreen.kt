package com.example.gustoria.ui.recipe

import androidx.compose.runtime.Composable
import com.example.gustoria.domain.RecipeRepoInterface

@Composable
fun EditRecipeScreen(
    recipeRepository: RecipeRepoInterface,
    recipeId: String?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    //
}