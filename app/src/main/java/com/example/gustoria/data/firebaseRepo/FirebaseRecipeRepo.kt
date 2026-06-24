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
            val now = System.currentTimeMillis()
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
                    createdAt = now - 86400000L * 165 // ~165 days ago
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
                    createdAt = now - 86400000L * 160 // ~160 days ago
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
                    createdAt = now - 86400000L * 155 // ~155 days ago
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
                    createdAt = now - 86400000L * 143 // ~143 days ago
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
                    createdAt = now - 86400000L * 134 // ~134 days ago
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
                    createdAt = now - 86400000L * 129 // ~129 days ago
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
                    createdAt = now - 86400000L * 115 // ~115 days ago
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
                    createdAt = now - 86400000L * 111 // ~111 days ago
                )
            )
            val placeholderRecipesV2 = listOf(
                // User 101 Recipes
                Recipe(
                    id = "r101_1",
                    ownerId = "101",
                    name = "Authentic Carbonara",
                    description = "The classic Roman pasta dish, made with just 4 ingredients.",
                    imageUri = "https://media-assets.lacucinaitaliana.it/photos/61fd250e7e33782b60f4b6c2/1:1/w_2560%2Cc_limit/Carbonara-classica.jpg",
                    cost = "€€",
                    difficulty = "Medium",
                    cuisineType = "Italian",
                    mealType = "Dinner",
                    cookingTimeMinutes = 20,
                    servings = 2,
                    //rating = 4.8f,
                    dietaryTags = emptyList(),
                    tags = listOf("Pasta", "Traditional", "Quick"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Spaghetti", quantity = 200, unit = "g"),
                        RecipeIngredient(name = "Guanciale", quantity = 100, unit = "g"),
                        RecipeIngredient(name = "Pecorino Romano", quantity = 50, unit = "g"),
                        RecipeIngredient(name = "Eggs", quantity = 2, unit = "large")
                    ),
                    steps = listOf(
                        "Boil salted water and cook spaghetti.",
                        "Brown the guanciale in a pan until crispy.",
                        "Whisk eggs and pecorino in a bowl.",
                        "Combine pasta, guanciale, and egg mixture away from heat. Serve immediately."
                    ),
                    likedByUserIds = listOf("102", "105"),
                ),
                Recipe(
                    id = "r101_2",
                    ownerId = "101",
                    name = "Classic Tiramisu",
                    description = "A coffee-flavored Italian dessert.",
                    imageUri = "https://www.giallozafferano.com/images/260-26067/Tiramisu_1200x800.jpg",
                    cost = "€€",
                    difficulty = "Medium",
                    cuisineType = "Italian",
                    mealType = "Dessert",
                    cookingTimeMinutes = 30,
                    servings = 6,
                    //rating = 4.9f,
                    dietaryTags = listOf("Vegetarian"),
                    tags = listOf("Sweet", "Coffee", "No-Bake"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Mascarpone", quantity = 250, unit = "g"),
                        RecipeIngredient(name = "Espresso", quantity = 1, unit = "cup"),
                        RecipeIngredient(name = "Ladyfingers", quantity = 200, unit = "g"),
                        RecipeIngredient(name = "Cocoa Powder", quantity = 2, unit = "tbsp")
                    ),
                    steps = listOf(
                        "Brew espresso and let it cool.",
                        "Whip mascarpone with sugar and eggs.",
                        "Dip ladyfingers briefly in espresso and layer them in a dish.",
                        "Spread mascarpone cream over the ladyfingers. Repeat layers.",
                        "Dust with cocoa powder and chill."
                    ),
                    likedByUserIds = listOf("103"),
                ),

                // User 102 Recipes
                Recipe(
                    id = "r102_1",
                    ownerId = "102",
                    name = "Spicy Chicken Tacos",
                    description = "Fiery tacos with homemade salsa.",
                    imageUri = "https://www.sprinklesandsprouts.com/wp-content/uploads/2024/09/Spicy-Chicken-Tacos-SQ.jpg",
                    cost = "€",
                    difficulty = "Easy",
                    cuisineType = "Mexican",
                    mealType = "Lunch",
                    cookingTimeMinutes = 25,
                    servings = 3,
                    //rating = 4.5f,
                    dietaryTags = listOf("Gluten-Free"),
                    tags = listOf("Spicy", "Street Food"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Corn Tortillas", quantity = 6, unit = "pieces"),
                        RecipeIngredient(name = "Chicken Breast", quantity = 300, unit = "g"),
                        RecipeIngredient(name = "Chili Powder", quantity = 1, unit = "tbsp"),
                        RecipeIngredient(name = "Lime", quantity = 1, unit = "piece")
                    ),
                    steps = listOf(
                        "Dice chicken and toss in chili powder and lime juice.",
                        "Cook chicken in a skillet until browned.",
                        "Warm the tortillas.",
                        "Assemble tacos and garnish with fresh cilantro."
                    ),
                    likedByUserIds = listOf("101", "104"),
                ),

                // User 103 Recipes
                Recipe(
                    id = "r103_1",
                    ownerId = "103",
                    name = "Creamy Vegan Mac and Cheese",
                    description = "Dairy-free comfort food made with a cashew base.",
                    imageUri = "https://bitofthegoodstuff.com/wp-content/uploads/2019/12/Vegan-Macaroni-Cheeze-2-1000.jpg",
                    cost = "€€",
                    difficulty = "Medium",
                    cuisineType = "American",
                    mealType = "Dinner",
                    cookingTimeMinutes = 40,
                    servings = 4,
                    //rating = 4.7f,
                    dietaryTags = listOf("Vegan", "Dairy-Free"),
                    tags = listOf("Comfort Food", "Healthy"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Macaroni", quantity = 250, unit = "g"),
                        RecipeIngredient(name = "Raw Cashews", quantity = 1, unit = "cup"),
                        RecipeIngredient(name = "Nutritional Yeast", quantity = 3, unit = "tbsp"),
                        RecipeIngredient(name = "Garlic Powder", quantity = 1, unit = "tsp")
                    ),
                    steps = listOf(
                        "Soak cashews in hot water for 20 minutes.",
                        "Cook macaroni according to package instructions.",
                        "Blend cashews, nutritional yeast, garlic powder, and water until smooth.",
                        "Mix the sauce with the pasta and heat through."
                    ),
                    likedByUserIds = listOf("101", "105"),
                ),
                Recipe(
                    id = "r103_2",
                    ownerId = "103",
                    name = "Avocado Toast with a Twist",
                    description = "Quick breakfast with secret spices.",
                    imageUri = "https://www.seriouseats.com/thmb/t5rugZ8T1CHzZo0ljUx7lWJNN2g=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/__opt__aboutcom__coeus__resources__content_migration__serious_eats__seriouseats.com__images__2016__05__20160502-avocado-toast-vicky-wasik-blue-cheese-3-9ce8acb777124a92bac3be2395742eca.jpg",
                    cost = "€",
                    difficulty = "Easy",
                    cuisineType = "Other",
                    mealType = "Breakfast",
                    cookingTimeMinutes = 5,
                    servings = 1,
                    //rating = 4.2f,
                    dietaryTags = listOf("Vegan", "Vegetarian"),
                    tags = listOf("Quick", "Morning"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Sourdough Bread", quantity = 1, unit = "slice"),
                        RecipeIngredient(name = "Avocado", quantity = 1, unit = "half"),
                        RecipeIngredient(name = "Chili Flakes", quantity = 1, unit = "pinch"),
                        RecipeIngredient(name = "Lemon Juice", quantity = 1, unit = "tsp")
                    ),
                    steps = listOf(
                        "Toast the sourdough bread.",
                        "Mash the avocado with lemon juice.",
                        "Spread over the toast and sprinkle with chili flakes."
                    ),
                    likedByUserIds = emptyList(),
                ),
                Recipe(
                    id = "r103_3",
                    ownerId = "103",
                    name = "Green Detox Smoothie",
                    description = "Start your day with a boost of greens.",
                    imageUri = "https://assets.tmecosys.com/image/upload/t_web_rdp_recipe_584x480/img/recipe/ras/Assets/7e9dbaae4d82710cb05cfd3eb173aae5/Derivates/a125a309099e85af661461978a981df3ccc37296.jpg",
                    cost = "€",
                    difficulty = "Easy",
                    cuisineType = "Other",
                    mealType = "Breakfast",
                    cookingTimeMinutes = 5,
                    servings = 1,
                    //rating = 4.0f,
                    dietaryTags = listOf("Vegan", "Gluten-Free"),
                    tags = listOf("Smoothie", "Detox"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Spinach", quantity = 1, unit = "handful"),
                        RecipeIngredient(name = "Green Apple", quantity = 1, unit = "piece"),
                        RecipeIngredient(name = "Ginger", quantity = 1, unit = "small piece"),
                        RecipeIngredient(name = "Water", quantity = 1, unit = "cup")
                    ),
                    steps = listOf(
                        "Chop the apple and ginger.",
                        "Place all ingredients in a blender.",
                        "Blend until completely smooth and serve cold."
                    ),
                    likedByUserIds = listOf("102"),
                ),

                // User 104 Recipes
                Recipe(
                    id = "r104_1",
                    ownerId = "104",
                    name = "Fudgy Brownies",
                    description = "The ultimate rich and dense chocolate brownies.",
                    imageUri = "https://www.afarmgirlsdabbles.com/wp-content/uploads/2025/05/Fudgy-Brownies_0043s-500x500.jpg",
                    cost = "€€",
                    difficulty = "Medium",
                    cuisineType = "American",
                    mealType = "Dessert",
                    cookingTimeMinutes = 45,
                    servings = 9,
                    //rating = 4.9f,
                    dietaryTags = emptyList(),
                    tags = listOf("Chocolate", "Baking", "Indulgent"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Dark Chocolate", quantity = 200, unit = "g"),
                        RecipeIngredient(name = "Butter", quantity = 150, unit = "g"),
                        RecipeIngredient(name = "Sugar", quantity = 200, unit = "g"),
                        RecipeIngredient(name = "Flour", quantity = 100, unit = "g")
                    ),
                    steps = listOf(
                        "Melt butter and chocolate together.",
                        "Whisk in sugar and eggs.",
                        "Fold in the flour gently.",
                        "Pour into a pan and bake at 180°C for 25 minutes."
                    ),
                    likedByUserIds = listOf("103", "105"),
                ),

                // User 105 Recipes
                Recipe(
                    id = "r105_1",
                    ownerId = "105",
                    name = "15-Minute Fried Rice",
                    description = "Use up your leftover rice for a quick dinner.",
                    imageUri = "https://cicili.tv/wp-content/uploads/2025/12/15-Min-Chicken-Fried-Rice-Small-1.jpg",
                    cost = "€",
                    difficulty = "Easy",
                    cuisineType = "Asian",
                    mealType = "Dinner",
                    cookingTimeMinutes = 15,
                    servings = 2,
                    //rating = 4.3f,
                    dietaryTags = listOf("Vegetarian"),
                    tags = listOf("Quick", "Leftovers"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Cooked Rice", quantity = 2, unit = "cups"),
                        RecipeIngredient(name = "Soy Sauce", quantity = 2, unit = "tbsp"),
                        RecipeIngredient(name = "Eggs", quantity = 2, unit = "pieces"),
                        RecipeIngredient(name = "Mixed Vegetables", quantity = 1, unit = "cup")
                    ),
                    steps = listOf(
                        "Scramble the eggs in a wok and set aside.",
                        "Stir-fry the vegetables until tender.",
                        "Add the rice and soy sauce, tossing constantly.",
                        "Mix the eggs back in and serve."
                    ),
                    likedByUserIds = listOf("101", "104"),
                ),
                Recipe(
                    id = "r105_2",
                    ownerId = "105",
                    name = "Mug Cake",
                    description = "When you need dessert in 3 minutes.",
                    imageUri = "https://images.immediate.co.uk/production/volatile/sites/30/2020/08/mug-cake-a012dbb.jpg",
                    cost = "€",
                    difficulty = "Easy",
                    cuisineType = "American",
                    mealType = "Dessert",
                    cookingTimeMinutes = 3,
                    servings = 1,
                    //rating = 4.1f,
                    dietaryTags = emptyList(),
                    tags = listOf("Microwave", "Late Night"),
                    ingredients = listOf(
                        RecipeIngredient(name = "Flour", quantity = 3, unit = "tbsp"),
                        RecipeIngredient(name = "Sugar", quantity = 2, unit = "tbsp"),
                        RecipeIngredient(name = "Cocoa Powder", quantity = 1, unit = "tbsp"),
                        RecipeIngredient(name = "Milk", quantity = 3, unit = "tbsp")
                    ),
                    steps = listOf(
                        "Mix all dry ingredients in a microwave-safe mug.",
                        "Stir in the milk until smooth.",
                        "Microwave on high for 90 seconds."
                    ),
                    likedByUserIds = emptyList(),
                )
            )

            firestore.runBatch { batch ->
                placeholderRecipesV2.forEach { recipe ->
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

    override fun getRecipesExcludingOwner(userId: String): Flow<List<Recipe>> {
        return recipesCollection
            .whereNotEqualTo("ownerId", userId)
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
