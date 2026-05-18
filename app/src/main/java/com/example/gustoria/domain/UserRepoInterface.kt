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

    // Return list of IDs of user's favourites
    fun getFavouriteRecipeIds(userId: String): Flow<List<String>>

    // Adds a recipe to favourite
    suspend fun addFavourite(userId: String, recipeId: String)

    // Removes recipe from favourites
    suspend fun removeFavourite(userId: String, recipeId: String)

    // True only if recipe is in user's favourites
    fun isFavourite(userId: String, recipeId: String): Flow<Boolean>

    // Return list of IDs of user's tried recipes
    fun getTriedRecipeIds(userId: String): Flow<List<String>>

    // Adds a recipe to tried list (Mark as Cooked)
    suspend fun addTriedRecipe(userId: String, recipeId: String)

    // Removes recipe from tried list
    suspend fun removeTriedRecipe(userId: String, recipeId: String)

    // True only if recipe is in user's tried list
    fun isTried(userId: String, recipeId: String): Flow<Boolean>
}