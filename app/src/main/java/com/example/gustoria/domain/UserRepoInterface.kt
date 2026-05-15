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

    // Restituisce elenco ID delle ricette preferite (favourites) di un utente
    fun getFavouriteRecipeIds(userId: String): Flow<List<String>>

    // Aggiunge una ricetta ai favourites dell'utente
    suspend fun addFavourite(userId: String, recipeId: String)

    // Rimuove una ricetta dai favourites
    suspend fun removeFavourite(userId: String, recipeId: String)

    //true se quella ricetta è nei favourites dell'utente
    fun isFavourite(userId: String, recipeId: String): Flow<Boolean>
}