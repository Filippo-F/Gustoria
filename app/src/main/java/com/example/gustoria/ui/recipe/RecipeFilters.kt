package com.example.gustoria.ui.recipe

import com.example.gustoria.Dataclass.Recipe

/*
 "at least 4 filtering elements":
  1. by name (search bar)
  2. by ingredient
  3. by cost range
  4. by difficulty
  5. by max cooking time (?)
*/

data class RecipeFilters(
    val nameQuery: String = "",
    val ingredientQuery: String = "",
    val selectedCosts: Set<String> = emptySet(),
    val selectedDifficulties: Set<String> = emptySet(),
    val minServings: Int? = null,
    val selectedTags: Set<String> = emptySet()
) {
    val isEmpty: Boolean get() =
        nameQuery.isBlank() && ingredientQuery.isBlank() &&
                selectedCosts.isEmpty() &&
                selectedDifficulties.isEmpty() &&
                minServings == null &&
                selectedTags.isEmpty()
}

val ALL_COSTS: List<String> = listOf("€", "€€", "€€€")
val ALL_DIFFICULTIES: List<String> = listOf("Easy", "Medium", "Hard")

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
        val servingsOk = filters.minServings?.let { recipe.servings >= it } ?: true
        val tagsOk = filters.selectedTags.isEmpty() ||
                (recipe.tags.intersect(filters.selectedTags)).isNotEmpty()

        nameOk && ingredientOk && costOk && difficultyOk && servingsOk && tagsOk
    }
}
