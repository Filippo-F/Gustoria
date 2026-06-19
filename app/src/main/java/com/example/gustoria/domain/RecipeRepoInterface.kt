package com.example.gustoria.domain

import com.example.gustoria.dataclass.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepoInterface {
    // Get all recipes on the platform
    fun getAllRecipes(): Flow<List<Recipe>>

    // Get a specific recipe by its ID
    fun getRecipeById(recipeId: String): Flow<Recipe?>

    // Get the recipes created by a specific user
    fun getRecipeByOwner(ownerId: String): Flow<List<Recipe>>

    // Get the recipes created by all users except the specified one
    fun getRecipesExcludingOwner(userId: String): Flow<List<Recipe>>

    // Insert a new recipe on the platform
    suspend fun addRecipe(recipe: Recipe)

    // Update an existing recipe
    suspend fun updateRecipe(recipeId: String, recipe: Recipe)

    // Delete a recipe from the platform
    suspend fun deleteRecipe(recipeId: String)

    // Aggiunge userId a likedByUserIds della ricetta
    suspend fun addLikedByUser(recipeId: String, userId: String)

    // Rimuove userId da likedByUserIds della ricetta
    suspend fun removeLikedByUser(recipeId: String, userId: String)

    // Somma totale dei like su tutte le ricette di un owner (per il profilo)
    fun getLikesCountForOwner(ownerId: String): Flow<Int>
}