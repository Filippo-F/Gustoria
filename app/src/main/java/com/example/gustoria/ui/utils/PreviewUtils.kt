package com.example.gustoria.ui.utils

import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.dataclass.Review
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.ReviewRepoInterface
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
        override suspend fun addLikedByUser(recipeId: String, userId: String) {}
        override suspend fun removeLikedByUser(recipeId: String, userId: String) {}
        override fun getLikesCountForOwner(ownerId: String): Flow<Int> = flowOf(0)
    }

    fun createFakeUserRepo() = object : UserRepoInterface {
        override fun getAllUsers(): Flow<List<User>> = flowOf(emptyList())
        override fun getUserById(userId: String): Flow<User?> = flowOf(
            User(
                internalId = userId,
                firstName = "Mario",
                lastName = "Rossi",
                nickname = "SuperChef",
                description = "Simple ingredients, great passion, amazing food.",
                phoneNumber = "+39 333 1234567",
                numberOfFollowers = 1200
            )
        )
        override suspend fun createUser(user: User) {}
        override suspend fun updateUser(userId: String, user: User) {}
        override suspend fun deleteUser(userId: String) {}
        override fun getFavouriteRecipeIds(userId: String): Flow<List<String>> = flowOf(emptyList())
        override suspend fun addFavourite(userId: String, recipeId: String) {}
        override suspend fun removeFavourite(userId: String, recipeId: String) {}
        override fun isFavourite(userId: String, recipeId: String): Flow<Boolean> = flowOf(false)
        override fun getTriedRecipeIds(userId: String): Flow<List<String>> = flowOf(emptyList())
        override suspend fun addTriedRecipe(userId: String, recipeId: String) {}
        override suspend fun removeTriedRecipe(userId: String, recipeId: String) {}
        override fun isTried(userId: String, recipeId: String): Flow<Boolean> = flowOf(false)
        override suspend fun getUsersWhoHaveInFavourites(recipeId: String): List<User> = emptyList()
        override suspend fun getUsersWhoHaveTried(recipeId: String): List<User> = emptyList()
    }

    fun createFakeReviewRepo() = object : ReviewRepoInterface {
        override fun getReviewsByRecipe(recipeId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getReviewsByUser(userId: String): Flow<List<Review>> = flowOf(emptyList())
        override fun getReviewById(reviewId: String): Flow<Review?> = flowOf(null)
        override fun getAllReviews(): Flow<List<Review>> = flowOf(emptyList())
        override suspend fun addReview(review: Review) {}
        override suspend fun updateReview(reviewId: String, review: Review) {}
        override suspend fun deleteReview(reviewId: String) {}
        override fun isLiked(userId: String, reviewId: String): Flow<Boolean> = flowOf(false)
        override suspend fun addLike(userId: String, reviewId: String) {}
        override suspend fun removeLike(userId: String, reviewId: String) {}
    }
}
