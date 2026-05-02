package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.SessionManager
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.recipe.RecipeFilters
import com.example.gustoria.ui.recipe.applyFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class RecipeViewModel(
    private val recipeRepository: RecipeRepoInterface
) : ViewModel() {

    val recipes: StateFlow<List<Recipe>> = recipeRepository.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _filters = MutableStateFlow(RecipeFilters())
    val filters: StateFlow<RecipeFilters> = _filters.asStateFlow()

    val filteredRecipes: StateFlow<List<Recipe>> =
        combine(recipes, _filters) { list, filters -> list.applyFilters(filters) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000), // o Eagerly?
                initialValue = emptyList()
            )

    private val _selectedRecipeId = MutableStateFlow<String?>(null)
    val selectedRecipe: StateFlow<Recipe?> =
        combine(recipes, _selectedRecipeId) { list, id -> list.find { it.id == id } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )


    fun isOwnedByCurrentUser(recipe: Recipe): Boolean =
        recipe.ownerId == SessionManager.CURRENT_LOGGED_IN_USER_ID

    fun selectRecipe(recipeId: String?) {
        _selectedRecipeId.value = recipeId
    }


    fun updateNameQuery(query: String) =
        _filters.update { it.copy(nameQuery = query) }

    fun updateIngredientQuery(query: String) =
        _filters.update { it.copy(ingredientQuery = query) }

    fun toggleDifficulty(difficulty: String) =
        _filters.update {
            val newSet = if (difficulty in it.selectedDifficulties) it.selectedDifficulties - difficulty
            else it.selectedDifficulties + difficulty
            it.copy(selectedDifficulties = newSet)
        }

    fun toggleCost(cost: String) =
        _filters.update {
            val newSet = if (cost in it.selectedCosts) it.selectedCosts - cost
            else it.selectedDifficulties + cost
            it.copy(selectedCosts = newSet)
        }

    fun clearFilters() {
        _filters.value = RecipeFilters()
    }


    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipeId)
            selectRecipe(null)
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    fun duplicateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val copy = recipe.copy(
                id = Uuid.random().toString(),
                ownerId = SessionManager.CURRENT_LOGGED_IN_USER_ID,
                name = "${recipe.name} (Copy)"
            )
            recipeRepository.addRecipe(copy)
        }
    }

    companion object {
        fun provideFactory(
            recipeRepository: RecipeRepoInterface
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    RecipeViewModel(recipeRepository) as T
            }
    }
}
