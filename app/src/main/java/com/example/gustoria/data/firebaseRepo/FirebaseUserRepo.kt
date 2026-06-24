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
import com.example.gustoria.ui.recipe.RecipeFilters
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FirebaseUserRepo(
    private val firestore: FirebaseFirestore
): UserRepoInterface {
    private val usersCollection = firestore.collection(Collections.USERS)

    suspend fun initializeData() {
        val existing = usersCollection.limit(1).get().await()
        if (existing.isEmpty) {
            /*val placeholdersUser = listOf(
                User(
                    internalId = "101",
                    nickname = "ChefMario",
                    firstName = "Mario",
                    lastName = "Rossi",
                    description = "Passionate about traditional Italian cuisine and fresh ingredients. Sharing family recipes passed down for generations.",
                    phoneNumber = "+39 123 4567890",
                    cookingRole = CookingRole.PROFESSIONAL_CHEF,
                    cuisinePreferences = listOf("Italian", "Mediterranean", "French"),
                    favouriteMealTypes = listOf("Dinner", "Dessert"),
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
                    favouriteMealTypes = listOf("Lunch", "Dinner", "Dessert"),
                    favouriteRecipesIds = listOf(
                        "recipe_spaghetti_pomodoro",
                        "recipe_margherita_pizza"
                    ),
                    triedRecipesIds = listOf("recipe_spaghetti_pomodoro"),
                    followingIds = listOf("101"),
                    numberOfFollowers = 1
                )
            )*/
            val placeholderUsersV2 = listOf(
                User(
                    internalId = "101",
                    nickname = "ChefAle",
                    firstName = "Alessandro",
                    lastName = "Rossi",
                    description = "Lover of traditional Italian cuisine.",
                    cookingRole = CookingRole.PROFESSIONAL_CHEF,
                    phoneNumber = "+39 333 1234567",
                    profileImageUri = "https://png.pngtree.com/png-vector/20230831/ourmid/pngtree-man-avatar-image-for-profile-png-image_9197908.png",
                    cuisinePreferences = listOf("Italian", "Mediterranean"),
                    dietaryRestrictions = emptyList(),
                    favouriteMealTypes = listOf("Lunch", "Dinner", "Dessert"),
                    favouriteRecipesIds = listOf("r103_1", "r105_2"),
                    triedRecipesIds = listOf("r102_1"),
                    followingIds = listOf("102", "104"),
                    numberOfFollowers = 2
                ),
                User(
                    internalId = "102",
                    nickname = "SpicyMia",
                    firstName = "Mia",
                    lastName = "Bianchi",
                    description = "Always looking for the next spicy challenge.",
                    cookingRole = CookingRole.HOME_COOK,
                    phoneNumber = "+39 333 7654321",
                    profileImageUri = "https://static.vecteezy.com/system/resources/previews/027/312/398/non_2x/portrait-of-a-female-journalist-isolated-essential-workers-avatar-icons-characters-for-social-media-and-networking-user-profile-website-and-app-3d-render-illustration-png.png",
                    cuisinePreferences = listOf("Mexican", "Indian", "Thai"),
                    dietaryRestrictions = listOf("Gluten-Free"),
                    favouriteMealTypes = listOf("Lunch", "Dinner"),
                    favouriteRecipesIds = emptyList(),
                    triedRecipesIds = listOf("r101_1"),
                    followingIds = listOf("101", "103"),
                    numberOfFollowers = 2
                ),
                User(
                    internalId = "103",
                    nickname = "GreenEats",
                    firstName = "Lorenzo",
                    lastName = "Verdi",
                    description = "Plant-based recipes for a sustainable future.",
                    cookingRole = CookingRole.CONTENT_CREATOR,
                    phoneNumber = "+39 333 1112223",
                    profileImageUri = "https://img.magnific.com/free-psd/3d-illustration-human-avatar-profile_23-2150671142.jpg",
                    cuisinePreferences = listOf("Vegan", "Healthy"),
                    dietaryRestrictions = listOf("Vegan", "Vegetarian"),
                    favouriteMealTypes = listOf("Breakfast", "Lunch"),
                    favouriteRecipesIds = listOf("r101_2"),
                    triedRecipesIds = listOf("r104_1"),
                    followingIds = listOf("105"),
                    numberOfFollowers = 2
                ),
                User(
                    internalId = "104",
                    nickname = "BakeMaster",
                    firstName = "Giulia",
                    lastName = "Romano",
                    description = "If it has sugar and butter, I'm baking it.",
                    cookingRole = CookingRole.FOOD_LOVER,
                    phoneNumber = "+39 333 4445556",
                    profileImageUri = "https://img.magnific.com/free-psd/3d-rendering-hair-style-avatar-design_23-2151869153.jpg",
                    cuisinePreferences = listOf("French", "American"),
                    dietaryRestrictions = emptyList(),
                    favouriteMealTypes = listOf("Dessert", "Breakfast"),
                    favouriteRecipesIds = listOf("r105_1"),
                    triedRecipesIds = emptyList(),
                    followingIds = listOf("101", "105"),
                    numberOfFollowers = 2
                ),
                User(
                    internalId = "105",
                    nickname = "QuickMeals",
                    firstName = "Davide",
                    lastName = "Ferrari",
                    description = "Fast, easy, and delicious meals for busy people.",
                    cookingRole = CookingRole.HOME_COOK,
                    phoneNumber = "+39 333 9998887",
                    profileImageUri = "https://freesvg.org/img/winkboy.png",
                    cuisinePreferences = listOf("Japanese", "Italian"),
                    dietaryRestrictions = emptyList(),
                    favouriteMealTypes = listOf("Snack", "Breakfast", "Lunch"),
                    favouriteRecipesIds = listOf("r101_1", "r103_2"),
                    triedRecipesIds = listOf("r103_1", "r104_1"),
                    followingIds = listOf("102", "103", "104"),
                    numberOfFollowers = 2
                )
            )

            firestore.runBatch { batch ->
                placeholderUsersV2.forEach { user ->
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
                    doc.toObject(RecentSearch::class.java)
                }
            }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addRecentSearch(userId: String, title: String, filters: RecipeFilters) {
        val collection = recentSearchesCollection(userId)

        // Avoid duplicate: remove any existing entry with the same title (case-insensitive)
        val existing = collection
            .whereEqualTo("title", title)
            .get().await()
        existing.documents.forEach { it.reference.delete().await() }

        // Enforce maximum of 5 recent searches — delete oldest if needed
        val all = collection
            .orderBy("searchedAt", Query.Direction.DESCENDING)
            .get().await()
        if (all.size() >= 5) {
            all.documents.drop(4).forEach { it.reference.delete().await() }
        }

        // Save the new entry
        val id = Uuid.random().toString()
        val entry = RecentSearch(
            id = id,
            title = title,
            filters = filters,
            searchedAt = System.currentTimeMillis()
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

    override suspend fun updatePushNotificationsEnabled(userId: String, enabled: Boolean) {
        usersCollection.document(userId)
            .update("pushNotificationsEnabled", enabled)
            .await()
    }

    override suspend fun updateNewRecipeAlertsEnabled(userId: String, enabled: Boolean) {
        usersCollection.document(userId)
            .update("newRecipeAlertsEnabled", enabled)
            .await()
    }

}
