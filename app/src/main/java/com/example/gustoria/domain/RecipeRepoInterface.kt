package com.example.gustoria.domain

import com.example.gustoria.Dataclass.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepoInterface {
    // Get all recipes on the platform
    fun getAllRecipes(): Flow<List<Recipe>>

    // Get a specific recipe by its ID
    fun getRecipeById(recipeId: String): Flow<Recipe?>

    // Get the recipes created by a specific user
    fun getRecipeByOwner(ownerId: String): Flow<List<Recipe>>

    // Insert a new recipe on the platform
    suspend fun addRecipe(recipe: Recipe)

    // Update an existing recipe
    suspend fun updateRecipe(recipeId: String, recipe: Recipe)

    // Delete a recipe from the platform
    suspend fun deleteRecipe(recipeId: String)
}