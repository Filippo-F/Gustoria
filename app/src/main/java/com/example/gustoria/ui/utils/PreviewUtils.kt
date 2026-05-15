package com.example.gustoria.ui.utils

import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
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

    fun createFakeUserRepo() = object : UserRepoInterface {
        override fun getAllUsers(): Flow<List<User>> = flowOf(emptyList())
        override fun getUserById(userId: String): Flow<User?> = flowOf(
            User(
                internalId = userId,
                fullName = "Mario Rossi",
                nickname = "SuperChef",
                email = "chef@gustoria.it",
                description = "Simple ingredients, great passion, amazing food.",
                phoneNumber = "+39 333 1234567",
                numberOfRecipes = 42,
                numberOfFollowers = 1200,
                numberOfLikes = 850
            )
        )
        override suspend fun createUser(user: User) {}
        override suspend fun updateUser(userId: String, user: User) {}
        override suspend fun deleteUser(userId: String) {}
    }
}
