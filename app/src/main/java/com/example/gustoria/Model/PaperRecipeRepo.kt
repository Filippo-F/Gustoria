package com.example.gustoria.model

import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.Flow

class PaperRecipeRepo: RecipeRepoInterface {
    override fun getAllRecipes(): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override fun getRecipeById(recipeId: String): Flow<Recipe?> {
        TODO("Not yet implemented")
    }

    override fun getRecipeByOwner(ownerId: String): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override suspend fun addRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRecipe(recipeId: String, recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(recipeId: String, recipe: Recipe) {
        TODO("Not yet implemented")
    }
}
