package com.example.gustoria.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.SessionManager
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnedRecipeViewModel(
    private val recipeRepository: RecipeRepoInterface
) : ViewModel() {

    private val ownerId: String = SessionManager.CURRENT_LOGGED_IN_USER_ID

    val ownedRecipes: StateFlow<List<Recipe>> =
        recipeRepository.getRecipeByOwner(ownerId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _filters = MutableStateFlow(RecipeFilters())
    val filters: StateFlow<RecipeFilters> = _filters.asStateFlow()

    val filteredRecipes: StateFlow<List<Recipe>> =
        combine(ownedRecipes, _filters) { list, filters -> list.applyFilters(filters) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun updateNameQuery(query: String) =
        _filters.update { it.copy(nameQuery = query) }

    fun updateIngredientQuery(query: String) =
        _filters.update { it.copy(ingredientQuery = query) }

    fun toggleCost(cost: String) =
        _filters.update {
            val newSet = if (cost in it.selectedCosts) it.selectedCosts - cost
            else it.selectedCosts + cost
            it.copy(selectedCosts = newSet)
        }

    fun toggleDifficulty(difficulty: String) =
        _filters.update {
            val newSet = if (difficulty in it.selectedDifficulties) it.selectedDifficulties - difficulty
            else it.selectedDifficulties + difficulty
            it.copy(selectedDifficulties = newSet)
        }

    fun clearFilters() {
        _filters.value = RecipeFilters()
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipeId)
        }
    }

    companion object {
        fun provideFactory(
            recipeRepository: RecipeRepoInterface
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OwnedRecipeViewModel(recipeRepository) as T
            }
    }
}
