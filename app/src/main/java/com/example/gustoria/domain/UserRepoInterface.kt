package com.example.gustoria.domain

import com.example.gustoria.dataclass.User
import kotlinx.coroutines.flow.Flow

interface UserRepoInterface {
    // Get all users on the platform
    fun getAllUsers(): Flow<List<User>>

    // Get a specific user by their unique ID
    fun getUserById(userId: String): Flow<User?>

    // Create a new user
    suspend fun createUser(user: User)

    // Update user profile information and preferences
    suspend fun updateUser(userId: String, user: User)

    // Delete specified user
    suspend fun deleteUser(userId: String)

    // Return list of IDs of user's favourites
    fun getFavouriteRecipeIds(userId: String): Flow<List<String>>

    // Adds a recipe to favourite
    suspend fun addFavourite(userId: String, recipeId: String)

    // Removes recipe from favourites
    suspend fun removeFavourite(userId: String, recipeId: String)

    // True only if recipe is in user's favourites
    fun isFavourite(userId: String, recipeId: String): Flow<Boolean>
}