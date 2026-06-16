package com.example.gustoria.data.firebaseRepo

import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.dataclass.RecipeIngredient
import com.example.gustoria.domain.Collections
import com.example.gustoria.domain.RecipeRepoInterface
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseRecipeRepo(
    private val firestore: FirebaseFirestore
) : RecipeRepoInterface {

    private val recipesCollection = firestore.collection(Collections.RECIPES)

    suspend fun initializeData() {
        val existing = recipesCollection.limit(1).get().await()
        if (existing.isEmpty) {
            val placeholderRecipes = listOf(
                // Ricette di User 101 (loggedUser)
                Recipe(
                    id = "recipe_spaghetti_pomodoro",
                    ownerId = "101",
                    name = "Spaghetti al Pomodoro",
                    description = "Un classico della cucina italiana, semplice e genuino.",
                    imageUri = null,
                    cost = "€",
                    difficulty = "Easy",
                    cuisineType = "Italian",
                    mealType = "Dinner",
                    dietaryTags = listOf("Vegan", "Dairy-Free"),
                    cookingTimeMinutes = 20,
                    servings = 2,
                    ingredients = listOf(
                        RecipeIngredient("Spaghetti", 200, "g"),
                        RecipeIngredient("Pomodori pelati", 400, "g"),
                        RecipeIngredient("Aglio", 2, "spicchi"),
                        RecipeIngredient("Olio EVO", 3, "cucchiai"),
                        RecipeIngredient("Basilico", 5, "foglie")
                    ),
                    steps = listOf(
                        "Porta a ebollizione l'acqua salata",
                        "Soffriggi l'aglio nell'olio",
                        "Aggiungi i pomodori e cuoci 15 min",
                        "Scola la pasta e condisci con il sugo",
                        "Aggiungi basilico fresco e servi"
                    ),
                    tags = listOf("Pasta", "Quick"),
                    likedByUserIds = listOf("202"),
                    createdAt = "2026-01-10T12:00:00"
                ),
                Recipe(
                    id = "recipe_margherita_pizza",
                    ownerId = "101",
                    name = "Pizza Margherita",
                    description = "La regina delle pizze italiane, con pomodoro, mozzarella e basilico.",
                    imageUri = null,
                    cost = "€",
                    difficulty = "Hard",
                    cuisineType = "Italian",
                    mealType = "Dinner",
                    dietaryTags = listOf("Vegetarian"),
                    cookingTimeMinutes = 90,
                    servings = 4,
                    ingredients = listOf(
                        RecipeIngredient("Farina 00", 500, "g"),
                        RecipeIngredient("Salsa di pomodoro", 200, "ml"),
                        RecipeIngredient("Mozzarella fior di latte", 250, "g"),
                        RecipeIngredient("Basilico", 5, "foglie"),
                        RecipeIngredient("Lievito di birra", 7, "g"),
                        RecipeIngredient("Olio EVO", 2, "cucchiai")
                    ),
                    steps = listOf(
                        "Impasta farina, acqua, lievito e sale",
                        "Lascia lievitare 2 ore",
                        "Stendi l'impasto e aggiungi il condimento",
                        "Cuoci in forno a 250°C per 10-12 min"
                    ),
                    tags = listOf("Pizza", "Classic"),
                    likedByUserIds = listOf("202"),
                    createdAt = "2026-01-15T18:30:00"
                ),
                Recipe(
                    id = "recipe_lasagna_bolognese",
                    ownerId = "202",
                    name = "Lasagna Bolognese",
                    description = "Strati di pasta, ragù ricco e besciamella cremosa.",
                    imageUri = null,
                    cost = "€€",
                    difficulty = "Hard",
                    cuisineType = "Italian",
                    mealType = "Dinner",
                    dietaryTags = emptyList(),
                    cookingTimeMinutes = 120,
                    servings = 6,
                    ingredients = listOf(
                        RecipeIngredient("Sfoglie di lasagna", 250, "g"),
                        RecipeIngredient("Ragù bolognese", 500, "ml"),
                        RecipeIngredient("Besciamella", 400, "ml"),
                        RecipeIngredient("Parmigiano Reggiano", 100, "g"),
                        RecipeIngredient("Burro", 20, "g")
                    ),
                    steps = listOf(
                        "Prepara il ragù (almeno 2 ore)",
                        "Prepara la besciamella",
                        "Alterna strati di pasta, ragù e besciamella",
                        "Termina con besciamella e parmigiano",
                        "Cuoci a 180°C per 40 min"
                    ),
                    tags = listOf("Pasta", "Baked"),
                    likedByUserIds = listOf("101"),
                    createdAt = "2026-01-20T19:00:00"
                ),
                Recipe(
                    id = "recipe_risotto_milanese",
                    ownerId = "202",
                    name = "Risotto alla Milanese",
                    description = "Risotto dorato allo zafferano, tipico di Milano.",
                    imageUri = null,
                    cost = "€€",
                    difficulty = "Medium",
                    cuisineType = "Italian",
                    mealType = "Dinner",
                    dietaryTags = listOf("Gluten-Free"),
                    cookingTimeMinutes = 30,
                    servings = 3,
                    ingredients = listOf(
                        RecipeIngredient("Riso Carnaroli", 300, "g"),
                        RecipeIngredient("Zafferano", 1, "bustina"),
                        RecipeIngredient("Brodo di carne", 1000, "ml"),
                        RecipeIngredient("Burro", 60, "g"),
                        RecipeIngredient("Cipolla", 1, "pc"),
                        RecipeIngredient("Vino bianco secco", 100, "ml")
                    ),
                    steps = listOf(
                        "Soffriggi la cipolla nel burro",
                        "Tosta il riso 2 min",
                        "Sfuma con il vino bianco",
                        "Aggiungi il brodo a mestoli, mescolando",
                        "Sciogli lo zafferano nel brodo e aggiungi",
                        "Manteca con burro e parmigiano"
                    ),
                    tags = listOf("Rice", "Classic"),
                    createdAt = "2026-02-01T20:00:00"
                ),
                Recipe(
                    id = "recipe_tiramisu",
                    ownerId = "202",
                    name = "Tiramisù",
                    description = "Il dessert italiano più famoso nel mondo.",
                    imageUri = null,
                    cost = "€€",
                    difficulty = "Easy",
                    cuisineType = "Italian",
                    mealType = "Dessert",
                    dietaryTags = listOf("Vegetarian"),
                    cookingTimeMinutes = 45,
                    servings = 8,
                    ingredients = listOf(
                        RecipeIngredient("Savoiardi", 200, "g"),
                        RecipeIngredient("Mascarpone", 500, "g"),
                        RecipeIngredient("Uova", 4, "pc"),
                        RecipeIngredient("Caffè espresso", 250, "ml"),
                        RecipeIngredient("Zucchero", 80, "g"),
                        RecipeIngredient("Cacao amaro in polvere", 20, "g")
                    ),
                    steps = listOf(
                        "Prepara il caffè e lascia raffreddare",
                        "Sbatti i tuorli con lo zucchero fino a crema",
                        "Incorpora il mascarpone ai tuorli",
                        "Monta gli albumi a neve e incorpora delicatamente",
                        "Immergi i savoiardi nel caffè e disponi a strati",
                        "Copri con crema e spolvera di cacao",
                        "Refrigera per almeno 4 ore"
                    ),
                    tags = listOf("NoBake"),
                    likedByUserIds = listOf("101"),
                    createdAt = "2026-02-10T16:00:00"
                ),
                // Ricette di User 202
                Recipe(
                    id = "recipe_sushi_rolls",
                    ownerId = "202",
                    name = "Sushi Rolls",
                    description = "Rotoli freschi di salmone e avocado.",
                    imageUri = null,
                    cost = "€€€",
                    difficulty = "Hard",
                    cuisineType = "Japanese",
                    mealType = "Lunch",
                    dietaryTags = listOf("Dairy-Free", "Gluten-Free"),
                    cookingTimeMinutes = 60,
                    servings = 2,
                    ingredients = listOf(
                        RecipeIngredient("Riso per sushi", 200, "g"),
                        RecipeIngredient("Nori", 2, "fogli"),
                        RecipeIngredient("Salmone fresco", 150, "g"),
                        RecipeIngredient("Avocado", 1, "pc"),
                        RecipeIngredient("Aceto di riso", 3, "cucchiai"),
                        RecipeIngredient("Salsa di soia", 4, "cucchiai")
                    ),
                    steps = listOf(
                        "Cuoci il riso e condisci con aceto di riso",
                        "Stendi il riso sul foglio di nori",
                        "Disponi salmone e avocado",
                        "Arrotola strettamente con la stuoia",
                        "Taglia in 8 pezzi e servi con soia"
                    ),
                    tags = listOf("Seafood", "Raw"),
                    likedByUserIds = listOf("101"),
                    createdAt = "2026-02-15T13:00:00"
                ),
                Recipe(
                    id = "recipe_beef_burger",
                    ownerId = "202",
                    name = "Beef Burger Artigianale",
                    description = "Burger succoso con carne macinata di qualità.",
                    imageUri = null,
                    cost = "€€",
                    difficulty = "Easy",
                    cuisineType = "American",
                    mealType = "Lunch",
                    dietaryTags = emptyList(),
                    cookingTimeMinutes = 15,
                    servings = 1,
                    ingredients = listOf(
                        RecipeIngredient("Manzo macinato", 200, "g"),
                        RecipeIngredient("Panino per burger", 1, "pc"),
                        RecipeIngredient("Cheddar", 1, "fetta"),
                        RecipeIngredient("Lattuga", 2, "foglie"),
                        RecipeIngredient("Pomodoro", 2, "fette")
                    ),
                    steps = listOf(
                        "Forma il patty e condisci con sale e pepe",
                        "Griglia 3-4 min per lato",
                        "Tosta il panino",
                        "Assembla con formaggio, lattuga e pomodoro"
                    ),
                    tags = listOf("Meat", "Quick"),
                    likedByUserIds = listOf("101"),
                    createdAt = "2026-03-01T12:30:00"
                ),
                Recipe(
                    id = "recipe_pad_thai",
                    ownerId = "202",
                    name = "Pad Thai",
                    description = "Il celebre street food thailandese con noodles e gamberi.",
                    imageUri = null,
                    cost = "€€",
                    difficulty = "Medium",
                    cuisineType = "Thai",
                    mealType = "Dinner",
                    dietaryTags = listOf("Dairy-Free"),
                    cookingTimeMinutes = 30,
                    servings = 2,
                    ingredients = listOf(
                        RecipeIngredient("Noodles di riso", 200, "g"),
                        RecipeIngredient("Gamberi", 150, "g"),
                        RecipeIngredient("Arachidi tostate", 30, "g"),
                        RecipeIngredient("Germogli di soia", 50, "g"),
                        RecipeIngredient("Uova", 2, "pc"),
                        RecipeIngredient("Salsa tamarindo", 3, "cucchiai"),
                        RecipeIngredient("Salsa di pesce", 2, "cucchiai")
                    ),
                    steps = listOf(
                        "Ammolla i noodles in acqua tiepida 20 min",
                        "Soffriggi i gamberi in wok",
                        "Aggiungi i noodles e le salse",
                        "Sposta di lato e strapazza le uova",
                        "Mescola tutto e servi con arachidi"
                    ),
                    tags = listOf("Noodles", "Spicy"),
                    createdAt = "2026-03-05T19:00:00"
                )
            )

            firestore.runBatch { batch ->
                placeholderRecipes.forEach { recipe ->
                    val docRef = recipesCollection.document(recipe.id)
                    batch.set(docRef, recipe)
                }
            }.await()
        }
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipesCollection
            .snapshots()
            .map { it.toObjects(Recipe::class.java) }
    }

    override fun getRecipeById(recipeId: String): Flow<Recipe?> {
        if (recipeId.isBlank()) return kotlinx.coroutines.flow.flowOf(null)
        return recipesCollection.document(recipeId)
            .snapshots()
            .map { it.toObject(Recipe::class.java) }
    }

    override fun getRecipeByOwner(ownerId: String): Flow<List<Recipe>> {
        return recipesCollection
            .whereEqualTo("ownerId", ownerId)
            .snapshots()
            .map { it.toObjects(Recipe::class.java) }
    }

    override suspend fun addRecipe(recipe: Recipe) {
        recipesCollection.document(recipe.id).set(recipe).await()
    }

    override suspend fun updateRecipe(recipeId: String, recipe: Recipe) {
        recipesCollection.document(recipeId).set(recipe).await()
    }

    override suspend fun deleteRecipe(recipeId: String) {
        recipesCollection.document(recipeId).delete().await()
    }

    override suspend fun addLikedByUser(recipeId: String, userId: String) {
        recipesCollection.document(recipeId)
            .update("likedByUserIds", FieldValue.arrayUnion(userId))
            .await()
    }

    override suspend fun removeLikedByUser(recipeId: String, userId: String) {
        recipesCollection.document(recipeId)
            .update("likedByUserIds", FieldValue.arrayRemove(userId))
            .await()
    }

    override fun getLikesCountForOwner(ownerId: String): Flow<Int> {
        return getRecipeByOwner(ownerId)
            .map { recipes -> recipes.sumOf { it.likedByUserIds.size } }
    }
}
