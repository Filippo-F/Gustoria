package com.example.gustoria.ui.recipe

import com.example.gustoria.dataclass.Recipe

data class RecipeFilters(
    val nameQuery: String = "",
    val ingredientQuery: String = "",
    val selectedCosts: Set<String> = emptySet(),
    val selectedDifficulties: Set<String> = emptySet(),
    val selectedCuisines: Set<String> = emptySet(),
    val selectedMealTypes: Set<String> = emptySet(),
    val selectedDietaryTags: Set<String> = emptySet(),
    /** null = no time limit; otherwise max cooking time in minutes. */
    val maxCookingTimeMinutes: Int? = null,
    val minServings: Int? = null,
) {
    val isEmpty: Boolean
        get() = nameQuery.isBlank() &&
                ingredientQuery.isBlank() &&
                selectedCosts.isEmpty() &&
                selectedDifficulties.isEmpty() &&
                selectedCuisines.isEmpty() &&
                selectedMealTypes.isEmpty() &&
                selectedDietaryTags.isEmpty() &&
                maxCookingTimeMinutes == null &&
                minServings == null
}

/**
 * Applies all active filters to this list of recipes.
 * Returns the full list when [filters] is empty.
 */
fun List<Recipe>.applyFilters(filters: RecipeFilters): List<Recipe> {
    if (filters.isEmpty) return this
    return filter { recipe ->
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

        val cuisineOk = filters.selectedCuisines.isEmpty() ||
                recipe.cuisineType in filters.selectedCuisines

        val mealTypeOk = filters.selectedMealTypes.isEmpty() ||
                recipe.mealType in filters.selectedMealTypes

        val dietaryOk = filters.selectedDietaryTags.isEmpty() ||
                recipe.dietaryTags.containsAll(filters.selectedDietaryTags)

        val timeOk = filters.maxCookingTimeMinutes == null ||
                recipe.cookingTimeMinutes <= filters.maxCookingTimeMinutes

        val servingsOk = filters.minServings?.let { recipe.servings >= it } ?: true

        nameOk && ingredientOk && costOk && difficultyOk &&
                cuisineOk && mealTypeOk && dietaryOk && timeOk && servingsOk
    }
}
