package com.example.gustoria.data.paperRepo

import com.example.gustoria.dataclass.CookingRole
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.Collections
import com.example.gustoria.domain.UserRepoInterface
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.distinctUntilChanged

class PaperUserRepo: UserRepoInterface {
    private val _placeholderUsers = listOf(
        User(
            internalId = "101",
            nickname = "User 101",
            firstName = "First",
            lastName = "User",
            phoneNumber = "+39 123 4567890",
            cookingRole = CookingRole.HOME_COOK,
            favouriteRecipesIds = listOf(
                "recipe_spaghetti_pomodoro",
                "recipe_lasagna_bolognese",
                "recipe_risotto_milanese",
                "recipe_margherita_pizza",
                "recipe_tiramisu"
            )
        ),
        User(
            internalId = "202",
            nickname = "User 202",
            firstName = "Second",
            lastName = "User",
            phoneNumber = "+39 098 7654321",
            cookingRole = CookingRole.FOOD_LOVER,
            favouriteRecipesIds = listOf(
                "recipe_sushi_rolls",
                "recipe_caesar_salad",
                "recipe_pad_thai",
                "recipe_french_onion_soup",
                "recipe_beef_burger"
            )
        )
    )

    private val userBook = Paper.book(Collections.USERS)

    private val _users = MutableStateFlow<List<User>>(loadInitialUsers())

    private fun loadInitialUsers(): List<User> {
        return try {
            userBook.allKeys.mapNotNull { key ->
                userBook.read<User>(key)
            }
        } catch (e: Exception) {
            userBook.destroy()
            emptyList()
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // First time load
    init {
        scope.launch {
            if (userBook.allKeys.isEmpty()) {
                _placeholderUsers.forEach { userBook.write(it.internalId, it) }
                _users.update { _placeholderUsers }
            }
        }
    }

    override fun getAllUsers(): Flow<List<User>> = _users.asStateFlow()


    override fun getUserById(userId: String): Flow<User?> =
        _users
            .map { list ->
                list.find { it.internalId == userId }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun createUser(user: User) = withContext(Dispatchers.IO) {
        userBook.write(user.internalId, user)
        _users.update { list ->
            list + user
        }
    }

    override suspend fun updateUser(
        userId: String,
        user: User
    ) = withContext(Dispatchers.IO) {
        userBook.write(userId, user)
        _users.update { list ->
            list.map { if (it.internalId == userId) user else it }
        }
    }

    override suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        userBook.delete(userId)
        _users.update { list ->
            list.filter { it.internalId != userId }
        }
    }

    //metodi per i favourites : get, add, remove e boolean (isFavourite: true or false)
    override fun getFavouriteRecipeIds(userId: String): Flow<List<String>> =
        _users
            .map { list -> list.find { it.internalId == userId }?.favouriteRecipesIds ?: emptyList() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun addFavourite(
        userId: String,
        recipeId: String
    ) = withContext(Dispatchers.IO) {
        val current = _users.value.find { it.internalId == userId } ?: return@withContext
        if (recipeId in current.favouriteRecipesIds) return@withContext
        val updated = current.copy(
            favouriteRecipesIds = current.favouriteRecipesIds + recipeId
        )
        userBook.write(userId, updated)
        _users.update { list ->
            list.map { if (it.internalId == userId) updated else it }
        }
    }

    override suspend fun removeFavourite(
        userId: String,
        recipeId: String
    ) = withContext(Dispatchers.IO) {
        val current = _users.value.find { it.internalId == userId } ?: return@withContext
        if (recipeId !in current.favouriteRecipesIds) return@withContext
        val updated = current.copy(
            favouriteRecipesIds = current.favouriteRecipesIds - recipeId
        )
        userBook.write(userId, updated)
        _users.update { list ->
            list.map { if (it.internalId == userId) updated else it }
        }
    }

    override fun isFavourite(
        userId: String,
        recipeId: String
    ): Flow<Boolean> =
        _users
            .map { list ->
                list.find { it.internalId == userId }
                    ?.favouriteRecipesIds
                    ?.contains(recipeId) == true
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)


    override fun getTriedRecipeIds(userId: String): Flow<List<String>> =
        _users
            .map { list -> list.find { it.internalId == userId }?.triedRecipesIds ?: emptyList() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun addTriedRecipe(
        userId: String,
        recipeId: String
    ) = withContext(Dispatchers.IO) {
        val current = _users.value.find { it.internalId == userId } ?: return@withContext
        if (recipeId in current.triedRecipesIds) return@withContext

        val updated = current.copy(
            triedRecipesIds = current.triedRecipesIds + recipeId
        )
        userBook.write(userId, updated)
        _users.update { list ->
            list.map { if (it.internalId == userId) updated else it }
        }
    }

    override suspend fun removeTriedRecipe(
        userId: String,
        recipeId: String
    ) = withContext(Dispatchers.IO) {
        val current = _users.value.find { it.internalId == userId } ?: return@withContext
        if (recipeId !in current.triedRecipesIds) return@withContext

        val updated = current.copy(
            triedRecipesIds = current.triedRecipesIds - recipeId
        )
        userBook.write(userId, updated)
        _users.update { list ->
            list.map { if (it.internalId == userId) updated else it }
        }
    }

    override fun isTried(
        userId: String,
        recipeId: String
    ): Flow<Boolean> =
        _users
            .map { list ->
                list.find { it.internalId == userId }
                    ?.triedRecipesIds
                    ?.contains(recipeId) == true
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}