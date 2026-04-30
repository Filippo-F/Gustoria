package com.example.gustoria.ui.recipe

import com.example.gustoria.Dataclass.Recipe

/**
 * UI state active filters
 *
 * "at least 4 filtering elements":
 *  1. by name (search bar)
 *  2. by ingredient
 *  3. by servings range
 *  4. by cost range
 *  5. by difficulty
 *  6. by max cooking time
 */
data class RecipeFilters(
    val nameQuery: String = "",
    val ingredientQuery: String = "",
    val selectedCosts: Set<String> = emptySet(),
    val selectedDifficulties: Set<String> = emptySet(),
    val minServings: Int? = null,
    val maxServings: Int? = null,
    val maxCookingTimeMinutes: Int? = null
) {
    val isEmpty: Boolean
        get() = nameQuery.isBlank() &&
                ingredientQuery.isBlank() &&
                selectedCosts.isEmpty() &&
                selectedDifficulties.isEmpty() &&
                minServings == null &&
                maxServings == null &&
                maxCookingTimeMinutes == null
}

val ALL_COSTS: List<String> = listOf("€", "€€", "€€€")

val ALL_DIFFICULTIES: List<String> = listOf("Easy", "Medium", "Hard")

/** [filters] to a list of recipes
 * [RecipeViewModel] (general list)
 * [OwnedRecipeViewModel] (user-owned list) */

fun List<Recipe>.applyFilters(filters: RecipeFilters): List<Recipe> {
    if (filters.isEmpty) return this
    return this.filter { recipe ->
        val nameOk = filters.nameQuery.isBlank() ||
                recipe.name.contains(filters.nameQuery, ignoreCase = true)
        val ingredientOk = filters.ingredientQuery.isBlank() ||
                recipe.ingredients.any {
                    it.name.contains(filters.ingredientQuery, ignoreCase = true)
                }
        val costOk = filters.selectedCosts.isEmpty() ||
                recipe.cost in filters.selectedCosts
        val difficultyOk = filters.selectedDifficulties.isEmpty() ||
                recipe.difficulty in filters.selectedDifficulties
        val minServingsOk = filters.minServings?.let { recipe.servings >= it } ?: true
        val maxServingsOk = filters.maxServings?.let { recipe.servings <= it } ?: true
        val timeOk = filters.maxCookingTimeMinutes
            ?.let { recipe.cookingTimeMinutes <= it } ?: true

        nameOk && ingredientOk && costOk && difficultyOk &&
                minServingsOk && maxServingsOk && timeOk
    }
}
