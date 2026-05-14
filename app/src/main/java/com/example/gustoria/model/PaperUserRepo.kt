package com.example.gustoria.model

import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.UserRepoInterface
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaperUserRepo: UserRepoInterface {
    private val _placeholderUsers = listOf(
        User(
            internalId = "101",
            nickname = "User 101",
            name = "First User",
            username = "user101",
            email = "user101@example.com"
        ),
        User(
            internalId = "202",
            nickname = "User 202",
            name = "Second User",
            username = "user202",
            email = "user202@example.com"
        )
    )

    private val userBook = Paper.book("users")

    // In memory state
    private val _users = MutableStateFlow<List<User>>(
        userBook.allKeys.mapNotNull { key ->
            userBook.read<User>(key)
        }
    )

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

    override suspend fun toggleFollow(currentUserId: String, targetUserId: String) {
        TODO("Not yet implemented")
    }

    override fun getCreatedRecipes(userId: String): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override fun getLikedRecipes(userId: String): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override fun getTriedRecipes(userId: String): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }
}