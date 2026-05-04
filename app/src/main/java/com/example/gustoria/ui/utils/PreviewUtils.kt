package com.example.gustoria.ui.utils

import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object PreviewUtils {
    fun createFakeRecipeRepo() = object : RecipeRepoInterface {
        override fun getAllRecipes(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getRecipeById(recipeId: String): Flow<Recipe?> = flowOf(
            Recipe(
                name = "Pasta al Pomodoro",
                description = "A classic Italian pasta dish with fresh tomatoes and basil.",
                cost = "€",
                difficulty = "Low",
                cookingTimeMinutes = 15,
                servings = 2,
                steps = listOf("Boil water", "Cook pasta", "Prepare sauce", "Mix and serve")
            )
        )
        override fun getRecipeByOwner(ownerId: String): Flow<List<Recipe>> = flowOf(emptyList())
        override suspend fun addRecipe(recipe: Recipe) {}
        override suspend fun updateRecipe(recipeId: String, recipe: Recipe) {}
        override suspend fun deleteRecipe(recipeId: String) {}
    }
}
