package com.example.gustoria.dataclass

import com.example.gustoria.ui.recipe.RecipeFilters

// Represents a single recent search entry for a user.

data class RecentSearch(
    val id: String = "",
    val title: String = "",
    val filters: RecipeFilters = RecipeFilters(),
    val searchedAt: Long = System.currentTimeMillis()
)
