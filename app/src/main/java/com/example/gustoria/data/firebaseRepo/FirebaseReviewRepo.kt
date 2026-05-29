package com.example.gustoria.data.firebaseRepo

import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.Collections
import com.example.gustoria.domain.ReviewRepoInterface
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

                // 4 recensioni per recipe_spaghetti_pomodoro (owned by user 101)
                Review(
                    id = "review_spaghetti_1",
                    userId = "202",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Semplicissima e deliziosa! Il sugo di pomodoro fresco fa tutta la differenza.",
                    rating = 5.0f,
                    likes = 4,
                    timestamp = (now - 86400000 * 7).toString()
                ),
                Review(
                    id = "review_spaghetti_2",
                    userId = "303",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Ottima ricetta, la faccio ogni settimana. Ho aggiunto un po' di peperoncino.",
                    rating = 4.5f,
                    likes = 2,
                    timestamp = (now - 86400000 * 5).toString()
                ),
                Review(
                    id = "review_spaghetti_3",
                    userId = "404",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Veloce e gustosa, perfetta per un pranzo last-minute. 10/10!",
                    rating = 5.0f,
                    likes = 6,
                    timestamp = (now - 86400000 * 3).toString()
                ),
                Review(
                    id = "review_spaghetti_4",
                    userId = "505",
                    recipeId = "recipe_spaghetti_pomodoro",
                    description = "Buona, ma ho preferito usare pomodorini invece dei pelati.",
                    rating = 4.0f,
                    likes = 1,
                    timestamp = (now - 86400000).toString()
                ),

                // 4 recensioni per recipe_sushi_rolls (owned by user 202)
                Review(
                    id = "review_sushi_1",
                    userId = "101",
                    recipeId = "recipe_sushi_rolls",
                    description = "Ottimo sushi casalingo! Difficile ma il risultato è fantastico.",
                    rating = 5.0f,
                    likes = 8,
                    timestamp = (now - 86400000 * 6).toString()
                ),
                Review(
                    id = "review_sushi_2",
                    userId = "303",
                    recipeId = "recipe_sushi_rolls",
                    description = "Ho seguito la ricetta passo passo, il riso era perfetto. Proverò con il tonno.",
                    rating = 4.5f,
                    likes = 3,
                    timestamp = (now - 86400000 * 4).toString()
                ),
                Review(
                    id = "review_sushi_3",
                    userId = "404",
                    recipeId = "recipe_sushi_rolls",
                    description = "Molto bello esteticamente, ma la tecnica di arrotolamento richiede pratica.",
                    rating = 4.0f,
                    likes = 2,
                    timestamp = (now - 86400000 * 2).toString()
                ),
                Review(
                    id = "review_sushi_4",
                    userId = "505",
                    recipeId = "recipe_sushi_rolls",
                    description = "Perfetto per una cena speciale! Ingredienti facilmente reperibili.",
                    rating = 5.0f,
                    likes = 5,
                    timestamp = (now - 3600000).toString()
                ),

                // Recensioni extra per altre ricette
                Review(
                    id = "review_pizza_1",
                    userId = "202",
                    recipeId = "recipe_margherita_pizza",
                    description = "Impasto meraviglioso, meglio di molte pizzerie!",
                    rating = 5.0f,
                    likes = 10,
                    timestamp = (now - 86400000 * 10).toString()
                ),
                Review(
                    id = "review_burger_1",
                    userId = "101",
                    recipeId = "recipe_beef_burger",
                    description = "La tecnica smash burger è perfetta, crosta croccante e interno succoso.",
                    rating = 4.5f,
                    likes = 7,
                    timestamp = (now - 86400000 * 2).toString()
                )
            )

            firestore.runBatch { batch ->
                placeholderReviews.forEach { review ->
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
}
