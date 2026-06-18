package com.example.gustoria.data.firebaseRepo

import com.example.gustoria.dataclass.CookingRole
import com.example.gustoria.dataclass.RecentSearch
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.Collections
import com.example.gustoria.domain.UserRepoInterface
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FirebaseUserRepo(
    private val firestore: FirebaseFirestore
): UserRepoInterface {
    private val usersCollection = firestore.collection(Collections.USERS)

    suspend fun initializeData() {
        val existing = usersCollection.limit(1).get().await()
        if (existing.isEmpty) {
            val placeholdersUser = listOf(
                User(
                    internalId = "101",
                    nickname = "ChefMario",
                    firstName = "Mario",
                    lastName = "Rossi",
                    description = "Passionate about traditional Italian cuisine and fresh ingredients. Sharing family recipes passed down for generations.",
                    phoneNumber = "+39 123 4567890",
                    cookingRole = CookingRole.PROFESSIONAL_CHEF,
                    cuisinePreferences = listOf("Italian", "Mediterranean", "French"),
                    favoriteIngredients = listOf("Tomato", "Basil", "Extra Virgin Olive Oil", "Parmesan"),
                    favouriteRecipesIds = listOf(
                        "recipe_lasagna_bolognese",
                        "recipe_tiramisu",
                        "recipe_sushi_rolls",
                        "recipe_beef_burger"
                    ),
                    triedRecipesIds = listOf("recipe_sushi_rolls", "recipe_beef_burger"),
                    followingIds = listOf("202"),
                    numberOfFollowers = 1
                ),
                User(
                    internalId = "202",
                    nickname = "SushiSara",
                    firstName = "Sara",
                    lastName = "Bianchi",
                    description = "Exploring world cuisines one recipe at a time. Specializing in Asian and fusion dishes.",
                    phoneNumber = "+39 987 6543210",
                    cookingRole = CookingRole.CONTENT_CREATOR,
                    cuisinePreferences = listOf("Japanese", "Thai", "American"),
                    favoriteIngredients = listOf("Salmon", "Avocado", "Soy Sauce", "Rice"),
                    favouriteRecipesIds = listOf(
                        "recipe_spaghetti_pomodoro",
                        "recipe_margherita_pizza"
                    ),
                    triedRecipesIds = listOf("recipe_spaghetti_pomodoro"),
                    followingIds = listOf("101"),
                    numberOfFollowers = 1
                )
            )

            firestore.runBatch { batch ->
                placeholdersUser.forEach { user ->
                    val docRef = usersCollection.document(user.internalId)
                    batch.set(docRef, user)
                }
            }.await()
        }
    }

    override fun getAllUsers(): Flow<List<User>> {
        return usersCollection
            .snapshots()
            .map { it.toObjects(User::class.java) }
    }

    override fun getUserById(userId: String): Flow<User?> {
        if (userId.isBlank()) return flowOf(null)
        return usersCollection.document(userId)
            .snapshots()
            .map { it.toObject<User>() }
    }

    override suspend fun createUser(user: User) {
        usersCollection
            .document(user.internalId)
            .set(user)
            .await()
    }

    override suspend fun updateUser(
        userId: String,
        user: User
    ) {
        usersCollection
            .document(userId)
            .set(user)
            .await()
    }

    override suspend fun deleteUser(userId: String) {
        usersCollection
            .document(userId)
            .delete()
            .await()
    }

    override fun getFavouriteRecipeIds(userId: String): Flow<List<String>> {
        return usersCollection.document(userId)
            .snapshots()
            .map { it.toObject(User::class.java)?.favouriteRecipesIds ?: emptyList() }
    }

    override suspend fun addFavourite(userId: String, recipeId: String) {
        usersCollection
            .document(userId)
            .update("favouriteRecipesIds", FieldValue.arrayUnion(recipeId))
            .await()
    }

    override suspend fun removeFavourite(userId: String, recipeId: String) {
        usersCollection
            .document(userId)
            .update("favouriteRecipesIds", FieldValue.arrayRemove(recipeId))
            .await()
    }

    override fun isFavourite(
        userId: String,
        recipeId: String
    ): Flow<Boolean> {
        return getFavouriteRecipeIds(userId).map { ids ->
            ids.contains(recipeId)
        }
    }

    override fun getTriedRecipeIds(userId: String): Flow<List<String>> {
        return usersCollection.document(userId)
            .snapshots()
            .map { it.toObject(User::class.java)?.triedRecipesIds ?: emptyList() }
    }

    override suspend fun addTriedRecipe(userId: String, recipeId: String) {
        usersCollection
            .document(userId)
            .update("triedRecipesIds", FieldValue.arrayUnion(recipeId))
            .await()
    }

    override suspend fun removeTriedRecipe(userId: String, recipeId: String) {
        usersCollection
            .document(userId)
            .update("triedRecipesIds", FieldValue.arrayRemove(recipeId))
            .await()
    }

    override fun isTried(
        userId: String,
        recipeId: String
    ): Flow<Boolean> {
        return getTriedRecipeIds(userId).map { ids ->
            ids.contains(recipeId)
        }
    }

    override suspend fun getUsersWhoHaveInFavourites(recipeId: String): List<User> {
        return usersCollection
            .whereArrayContains("favouriteRecipesIds", recipeId)
            .get().await()
            .toObjects(User::class.java)
    }

    override suspend fun getUsersWhoHaveTried(recipeId: String): List<User> {
        return usersCollection
            .whereArrayContains("triedRecipesIds", recipeId)
            .get().await()
            .toObjects(User::class.java)
    }

    override suspend fun followUser(currentUserId: String, targetUserId: String) {
        usersCollection.document(currentUserId)
            .update("followingIds", FieldValue.arrayUnion(targetUserId))
            .await()
        usersCollection.document(targetUserId)
            .update("numberOfFollowers", FieldValue.increment(1))
            .await()
    }

    override suspend fun unfollowUser(currentUserId: String, targetUserId: String) {
        usersCollection.document(currentUserId)
            .update("followingIds", FieldValue.arrayRemove(targetUserId))
            .await()
        val targetDoc = usersCollection.document(targetUserId).get().await()
        val currentFollowers = targetDoc.getLong("numberOfFollowers") ?: 0L
        if (currentFollowers > 0) {
            usersCollection.document(targetUserId)
                .update("numberOfFollowers", FieldValue.increment(-1))
                .await()
        }
    }

    override fun isFollowing(currentUserId: String, targetUserId: String): Flow<Boolean> {
        return usersCollection.document(currentUserId)
            .snapshots()
            .map { it.toObject(User::class.java)?.followingIds?.contains(targetUserId) == true }
    }

    private fun recentSearchesCollection(userId: String) =
        usersCollection.document(userId).collection(Collections.RECENT_SEARCHES)

    override fun getRecentSearches(userId: String): Flow<List<RecentSearch>> {
        return recentSearchesCollection(userId)
            .orderBy("searchedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    RecentSearch(
                        id = doc.getString("id") ?: doc.id,
                        title = doc.getString("title") ?: "",
                        searchedAt = doc.getLong("searchedAt") ?: System.currentTimeMillis()
                    )
                }
            }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addRecentSearch(userId: String, title: String) {
        val collection = recentSearchesCollection(userId)

        // Avoid duplicate: remove any existing entry with the same title (case-insensitive)
        val existing = collection
            .whereEqualTo("title", title)
            .get().await()
        existing.documents.forEach { it.reference.delete().await() }

        // Enforce maximum of 10 recent searches — delete oldest if needed
        val all = collection
            .orderBy("searchedAt", Query.Direction.DESCENDING)
            .get().await()
        if (all.size() >= 10) {
            all.documents.drop(9).forEach { it.reference.delete().await() }
        }

        // Save the new entry
        val id = Uuid.random().toString()
        val entry = mapOf(
            "id" to id,
            "title" to title,
            "searchedAt" to System.currentTimeMillis()
        )
        collection.document(id).set(entry).await()
    }

    override suspend fun removeRecentSearch(userId: String, searchId: String) {
        recentSearchesCollection(userId).document(searchId).delete().await()
    }

    override suspend fun clearAllRecentSearches(userId: String) {
        val docs = recentSearchesCollection(userId).get().await()
        firestore.runBatch { batch ->
            docs.documents.forEach { batch.delete(it.reference) }
        }.await()
    }

}
