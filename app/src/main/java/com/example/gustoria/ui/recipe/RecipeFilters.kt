package com.example.gustoria.ui.recipe

import com.example.gustoria.dataclass.Recipe

data class RecipeFilters(
    val nameQuery: String = "",
    val ingredientQuery: String = "",
    val selectedCosts: List<String> = emptyList(),
    val selectedDifficulties: List<String> = emptyList(),
    val selectedCuisines: List<String> = emptyList(),
    val selectedMealTypes: List<String> = emptyList(),
    val selectedDietaryTags: List<String> = emptyList(),
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

    val displayTitle: String
        get() {
            if (nameQuery.isNotBlank()) return nameQuery
            if (ingredientQuery.isNotBlank()) return "Ingredients: $ingredientQuery"
            
            val activeTags = selectedCosts + selectedDifficulties + selectedCuisines + selectedMealTypes + selectedDietaryTags
            val tagsString = activeTags.joinToString(" | ")
            
            if (tagsString.isNotBlank()) return tagsString
            if (maxCookingTimeMinutes != null) return "≤ $maxCookingTimeMinutes min"
            if (minServings != null) return "≥ $minServings servings"
            
            return "All Recipes"
        }
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
