package com.example.gustoria.domain

import com.example.gustoria.dataclass.User
import kotlinx.coroutines.flow.Flow

interface UserRepoInterface {
    //get all users
    fun getAllUsers(): Flow<List<User>>

    //get a specific user by unique ID
    fun getUserById(userId: String): Flow<User?>

    // create a new user
    suspend fun createUser(user: User)

    // update user profile infos and preferences
    suspend fun updateUser(userId: String, user: User)

    // delete user
    suspend fun deleteUser(userId: String)

    // restituisce elenco ID delle ricette preferite (favourites) di un utente
    fun getFavouriteRecipeIds(userId: String): Flow<List<String>>

    // aggiunge una ricetta ai favourites dell'utente
    suspend fun addFavourite(userId: String, recipeId: String)

    //rimuove una ricetta dai favourites
    suspend fun removeFavourite(userId: String, recipeId: String)

    //true se quella ricetta è nei favourites dell'utente
    fun isFavourite(userId: String, recipeId: String): Flow<Boolean>
}