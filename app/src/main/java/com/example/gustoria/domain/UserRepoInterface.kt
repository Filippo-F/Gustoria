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
}