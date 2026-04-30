package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.model.PaperRecipeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RecipeListViewModel(
    // Manual Dependency Injection
    private val repository: RecipeRepoInterface = PaperRecipeRepo()
) : ViewModel() {

    // Query string for recipe name or ingredients
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Null means "No filter selected" (shows all)
    private val _selectedCost = MutableStateFlow<String?>(null)
    val selectedCost = _selectedCost.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    private val _minServings = MutableStateFlow<Int?>(null)
    val minServings = _minServings.asStateFlow()

    // Filtered list
    val filteredRecipes: StateFlow<List<Recipe>> = combine(
        repository.getAllRecipes(),
        _searchQuery,
        _selectedCost,
        _selectedDifficulty,
        _minServings
    ) { recipes, query, cost, difficulty, servings ->
        recipes.filter { recipe ->
            // Filter 1: Ingredients & Name
            val matchesQuery = if (query.isBlank()) true else {
                recipe.name.contains(query, ignoreCase = true) ||
                        recipe.ingredients.any { it.name.contains(query, ignoreCase = true) }
            }

            // Filter 2: Cost
            val matchesCost = if (cost == null) true else recipe.cost == cost

            // Filter 3: Difficulty
            val matchesDifficulty = if (difficulty == null) true else recipe.difficulty == difficulty

            // Filter 4: Servings
            val matchesServings = if (servings == null) true else recipe.servings >= servings

            // Keep the recipe only if it passes all active filters
            matchesQuery && matchesCost && matchesDifficulty && matchesServings
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList() // Default empty state while loading
    )

    // Actions triggered by UI
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCostFilterChange(cost: String?) {
        // If they click the already selected cost, unselect it
        _selectedCost.value = if (_selectedCost.value == cost) null else cost
    }

    fun onDifficultyFilterChange(difficulty: String?) {
        _selectedDifficulty.value = if (_selectedDifficulty.value == difficulty) null else difficulty
    }

    fun onServingsFilterChange(servings: Int?) {
        _minServings.value = if (_minServings.value == servings) null else servings
    }
}