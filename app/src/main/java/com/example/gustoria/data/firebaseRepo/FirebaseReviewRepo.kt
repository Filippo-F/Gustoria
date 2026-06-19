package com.example.gustoria.data.firebaseRepo

import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.Collections
import com.example.gustoria.domain.ReviewRepoInterface
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseReviewRepo(
    private val firestore: FirebaseFirestore
) : ReviewRepoInterface {

    private val reviewsCollection = firestore.collection(Collections.REVIEWS)

    suspend fun initializeData() {
        val existing = reviewsCollection.limit(1).get().await()
        if (existing.isEmpty) {

            val now = System.currentTimeMillis()
            val placeholderReviews = listOf(

                // 4 reviews for recipe_spaghetti_pomodoro (owned by 101)
                Review(
                    id = "review_spaghetti_1",
                    userId = "202",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Semplicissima e deliziosa! Il sugo di pomodoro fresco fa tutta la differenza.",
                    rating = 5.0f,
                    likedByUserIds = listOf("101"), // 202 scrive, 101 mette like
                    timestamp = (now - 86400000 * 7)
                ),
                Review(
                    id = "review_spaghetti_2",
                    userId = "202",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Ottima ricetta, la faccio ogni settimana. Ho aggiunto un po' di peperoncino.",
                    rating = 4.5f,
                    likedByUserIds = listOf("101"),
                    timestamp = (now - 86400000 * 5)
                ),
                Review(
                    id = "review_spaghetti_3",
                    userId = "202",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Veloce e gustosa, perfetta per un pranzo last-minute. 10/10!",
                    rating = 5.0f,
                    likedByUserIds = listOf("101"),
                    timestamp = (now - 86400000 * 3)
                ),
                Review(
                    id = "review_spaghetti_4",
                    userId = "202",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Buona, ma ho preferito usare pomodorini invece dei pelati.",
                    rating = 4.0f,
                    timestamp = (now - 86400000)
                ),

                // 4 reviews for recipe_sushi_rolls (owned by 202)
                Review(
                    id = "review_sushi_1",
                    userId = "101",
                    recipeId = "recipe_sushi_rolls",
                    description = "Ottimo sushi casalingo! Difficile ma il risultato è fantastico.",
                    rating = 5.0f,
                    likedByUserIds = listOf("202"), // 101 scrive, 202 mette like
                    timestamp = (now - 86400000 * 6)
                ),
                Review(
                    id = "review_sushi_2",
                    userId = "101",
                    recipeId = "recipe_sushi_rolls",
                    description = "Ho seguito la ricetta passo passo, il riso era perfetto. Proverò con il tonno.",
                    rating = 4.5f,
                    likedByUserIds = listOf("202"),
                    timestamp = (now - 86400000 * 4)
                ),
                Review(
                    id = "review_sushi_3",
                    userId = "101",
                    recipeId = "recipe_sushi_rolls",
                    description = "Molto bello esteticamente, ma la tecnica di arrotolamento richiede pratica.",
                    rating = 4.0f,
                    timestamp = (now - 86400000 * 2)
                ),
                Review(
                    id = "review_sushi_4",
                    userId = "101",
                    recipeId = "recipe_sushi_rolls",
                    description = "Perfetto per una cena speciale! Ingredienti facilmente reperibili.",
                    rating = 5.0f,
                    likedByUserIds = listOf("202"),
                    timestamp = (now - 3600000)
                ),

                // Extra reviews for other recipes
                Review(
                    id = "review_pizza_1",
                    userId = "202",
                    recipeId = "recipe_margherita_pizza",
                    description = "Impasto meraviglioso, meglio di molte pizzerie!",
                    rating = 5.0f,
                    likedByUserIds = listOf("101"),
                    timestamp = (now - 86400000 * 10)
                ),
                Review(
                    id = "review_burger_1",
                    userId = "101",
                    recipeId = "recipe_beef_burger",
                    description = "La tecnica smash burger è perfetta, crosta croccante e interno succoso.",
                    rating = 4.5f,
                    likedByUserIds = listOf("202"),
                    timestamp = (now - 86400000 * 2)
                )
            )
            val placeholderReviewsV2 = listOf(
                // Reviews for r101_1 (3 reviews)
                Review(
                    id = "rev_1",
                    userId = "102",
                    recipeId = "r101_1",
                    description = "Absolutely perfect! Tastes just like my trip to Rome.",
                    rating = 5.0f,
                    likedByUserIds = listOf("104", "105"),
                    photoUri = null,
                ),
                Review(
                    id = "rev_2",
                    userId = "104",
                    recipeId = "r101_1",
                    description = "A bit too salty for me, but the texture was great.",
                    rating = 3.0f,
                    likedByUserIds = emptyList(),
                    photoUri = null,
                ),
                Review(
                    id = "rev_3",
                    userId = "105",
                    recipeId = "r101_1",
                    description = "So easy to make! Added this to my weekly rotation.",
                    rating = 5.0f,
                    likedByUserIds = listOf("101"),
                    photoUri = null,
                ),

                // Reviews for r101_2 (0 reviews) - INTENTIONALLY LEFT EMPTY

                // Reviews for r102_1 (1 review)
                Review(
                    id = "rev_4",
                    userId = "101",
                    recipeId = "r102_1",
                    description = "Loved the spice level! I used habanero instead of standard chili powder.",
                    rating = 4.0f,
                    likedByUserIds = listOf("102"),
                    photoUri = null,
                ),

                // Reviews for r103_1 (2 reviews)
                Review(
                    id = "rev_5",
                    userId = "105",
                    recipeId = "r103_1",
                    description = "Didn't think a vegan cheese sauce could be this creamy. Amazing.",
                    rating = 5.0f,
                    likedByUserIds = listOf("103"),
                    photoUri = null,
                ),
                Review(
                    id = "rev_6",
                    userId = "101",
                    recipeId = "r103_1",
                    description = "As a traditional chef, I was skeptical, but this is a solid dish.",
                    rating = 4.0f,
                    likedByUserIds = emptyList(),
                    photoUri = null,
                ),

                // Reviews for r103_2 (0 reviews) - INTENTIONALLY LEFT EMPTY

                // Reviews for r103_3 (1 review)
                Review(
                    id = "rev_7",
                    userId = "102",
                    recipeId = "r103_3",
                    description = "Very refreshing, but a bit too ginger-heavy for my morning.",
                    rating = 3.0f,
                    likedByUserIds = emptyList(),
                    photoUri = null,
                ),

                // Reviews for r104_1 (2 reviews)
                Review(
                    id = "rev_8",
                    userId = "103",
                    recipeId = "r104_1",
                    description = "I swapped the butter for coconut oil and it still turned out great!",
                    rating = 4.0f,
                    likedByUserIds = listOf("104"),
                    photoUri = null,
                ),
                Review(
                    id = "rev_9",
                    userId = "105",
                    recipeId = "r104_1",
                    description = "The fudgiest brownies I have ever made. 10/10.",
                    rating = 5.0f,
                    likedByUserIds = listOf("101", "102"),
                    photoUri = null,
                ),

                // Reviews for r105_1 (3 reviews)
                Review(
                    id = "rev_10",
                    userId = "101",
                    recipeId = "r105_1",
                    description = "Good base recipe. I threw in some leftover pancetta and it was fantastic.",
                    rating = 4.0f,
                    likedByUserIds = listOf("105"),
                    photoUri = null,
                ),
                Review(
                    id = "rev_11",
                    userId = "102",
                    recipeId = "r105_1",
                    description = "Quick and easy! Splashed some sriracha on top for extra heat.",
                    rating = 4.0f,
                    likedByUserIds = emptyList(),
                    photoUri = null,
                ),
                Review(
                    id = "rev_12",
                    userId = "104",
                    recipeId = "r105_1",
                    description = "Saved my life after a long shift. Simple and tasty.",
                    rating = 5.0f,
                    likedByUserIds = listOf("103", "105"),
                    photoUri = null,
                ),

                // Reviews for r105_2 (0 reviews) - INTENTIONALLY LEFT EMPTY
            )

            firestore.runBatch { batch ->
                placeholderReviewsV2.forEach { review ->
                    val docRef = reviewsCollection.document(review.id)
                    batch.set(docRef, review)
                }
            }.await()
        }
    }

    override fun getReviewsByRecipe(recipeId: String): Flow<List<Review>> {
        return reviewsCollection
            .whereEqualTo("recipeId", recipeId)
            .snapshots()
            .map { it.toObjects(Review::class.java) }
    }

    override fun getReviewsByUser(userId: String): Flow<List<Review>> {
        return reviewsCollection
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { it.toObjects(Review::class.java) }
    }

    override fun getReviewById(reviewId: String): Flow<Review?> {
        return reviewsCollection.document(reviewId)
            .snapshots()
            .map { it.toObject(Review::class.java) }
    }

    override fun getAllReviews(): Flow<List<Review>> {
        return reviewsCollection
            .snapshots()
            .map { it.toObjects(Review::class.java) }
    }

    override suspend fun addReview(review: Review) {
        reviewsCollection.document(review.id).set(review).await()
    }

    override suspend fun updateReview(reviewId: String, review: Review) {
        reviewsCollection.document(reviewId).set(review).await()
    }

    override suspend fun deleteReview(reviewId: String) {
        reviewsCollection.document(reviewId).delete().await()
    }

    override fun isLiked(userId: String, reviewId: String): Flow<Boolean> {
        return getReviewById(reviewId)
            .map { review -> review?.likedByUserIds?.contains(userId) == true }
    }

    override suspend fun addLike(userId: String, reviewId: String) {
        reviewsCollection.document(reviewId)
            .update("likedByUserIds", FieldValue.arrayUnion(userId))
            .await()
    }

    override suspend fun removeLike(userId: String, reviewId: String) {
        reviewsCollection.document(reviewId)
            .update("likedByUserIds", FieldValue.arrayRemove(userId))
            .await()
    }
}
