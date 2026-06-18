package com.example.gustoria.domain

import com.example.gustoria.dataclass.User
import com.example.gustoria.dataclass.RecentSearch
import kotlinx.coroutines.flow.Flow

interface UserRepoInterface {
    // Get all users
    fun getAllUsers(): Flow<List<User>>

    // Get a specific user by unique ID
    fun getUserById(userId: String): Flow<User?>

    // Create a new user
    suspend fun createUser(user: User)

    // Update user profile infos and preferences
    suspend fun updateUser(userId: String, user: User)

    // Delete user
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

    // Restituisce tutti gli utenti che hanno recipeId nei preferiti (per cascade delete)
    suspend fun getUsersWhoHaveInFavourites(recipeId: String): List<User>

    // Restituisce tutti gli utenti che hanno recipeId nei tried (per cascade delete)
    suspend fun getUsersWhoHaveTried(recipeId: String): List<User>

    // Il loggedUser inizia a seguire targetUserId
    suspend fun followUser(currentUserId: String, targetUserId: String)

    // Il loggedUser smette di seguire targetUserId
    suspend fun unfollowUser(currentUserId: String, targetUserId: String)

    // True se il loggedUser segue già targetUserId
    fun isFollowing(currentUserId: String, targetUserId: String): Flow<Boolean>

    // Ritorna le ricerche recenti dell'utente in tempo reale, ordinate dalla più recente
    fun getRecentSearches(userId: String): Flow<List<RecentSearch>>

    // Aggiunge una ricerca recente; rimuove automaticamente la più vecchia se si supera il limite di 10
    suspend fun addRecentSearch(userId: String, title: String)

    // Rimuove una singola ricerca recente tramite il suo ID
    suspend fun removeRecentSearch(userId: String, searchId: String)

    // Cancella tutte le ricerche recenti dell'utente
    suspend fun clearAllRecentSearches(userId: String)
}
