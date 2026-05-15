package com.example.gustoria.model

import com.example.gustoria.dataclass.CookingRole
import com.example.gustoria.dataclass.Recipe
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
            fullName = "First User",
            username = "user101",
            email = "user101@example.com",
            phoneNumber = "+39 123 4567890",
            cookingRole = CookingRole.HOME_COOK
        ),
        User(
            internalId = "202",
            nickname = "User 202",
            fullName = "Second User",
            username = "user202",
            email = "user202@example.com",
            phoneNumber = "+39 098 7654321",
            cookingRole = CookingRole.FOOD_LOVER
        )
    )

    private val userBook = Paper.book("users")

    // In memory state
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
}