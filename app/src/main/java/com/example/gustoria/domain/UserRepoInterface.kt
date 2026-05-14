package com.example.gustoria.domain

import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.dataclass.User
import kotlinx.coroutines.flow.Flow

interface UserRepoInterface {
    // Get all users on the platform
    fun getAllUsers(): Flow<List<User>>

    // Get a specific user by their unique ID
    fun getUserById(userId: String): Flow<User?>

    // Update user profile information and preferences
    suspend fun updateUser(userId: String, user: User)

    // Delete specified user
    suspend fun deleteUser(userId: String)

    // Follow or unfollow a user
    suspend fun toggleFollow(currentUserId: String, targetUserId: String)

    // Get recipes created by a specific user
    fun getCreatedRecipes(userId: String): Flow<List<Recipe>>

    // Get recipes liked by a specific user
    fun getLikedRecipes(userId: String): Flow<List<Recipe>>

    // Get recipes tried by a specific user
    fun getTriedRecipes(userId: String): Flow<List<Recipe>>
}