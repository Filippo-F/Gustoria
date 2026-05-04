package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.SessionManager
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.recipe.RecipeFilters
import com.example.gustoria.ui.recipe.applyFilters
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class OwnedRecipeViewModel(
    private val repo: RecipeRepoInterface
) : ViewModel() {

    private val userId = SessionManager.CURRENT_LOGGED_IN_USER_ID

    private val _filters = MutableStateFlow(RecipeFilters())
    val filters: StateFlow<RecipeFilters> = _filters.asStateFlow()

    private val myRecipes: StateFlow<List<Recipe>> =
        repo.getRecipeByOwner(userId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val recipesToShow: StateFlow<List<Recipe>> = combine(
        myRecipes, _filters
    ) { list, f ->
        list.applyFilters(f)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe

    fun selectRecipe(recipe: Recipe?) {
        _selectedRecipe.value = recipe
    }

    fun setNameQuery(q: String) {
        _filters.update { it.copy(nameQuery = q) }
    }

    fun setIngredientQuery(q: String) {
        _filters.update { it.copy(ingredientQuery = q) }
    }

    fun toggleCost(c: String) {
        _filters.update {
            val newSet = if (c in it.selectedCosts) it.selectedCosts - c else it.selectedCosts + c
            it.copy(selectedCosts = newSet)
        }
    }

    fun toggleDifficulty(d: String) {
        _filters.update {
            val newSet = if (d in it.selectedDifficulties) it.selectedDifficulties - d else it.selectedDifficulties + d
            it.copy(selectedDifficulties = newSet)
        }
    }

    fun resetFilters() {
        _filters.value = RecipeFilters()
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.deleteRecipe(id)
            if (_selectedRecipe.value?.id == id) {
                _selectedRecipe.value = null
            }
        }
    }

    fun duplicateRecipe(recipe: Recipe, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            val newId = Uuid.random().toString()
            val duplicatedRecipe = recipe.copy(
                id = newId,
                ownerId = userId,
                name = if (recipe.name.endsWith(" (Copy)")) recipe.name else "${recipe.name} (Copy)"
            )
            repo.addRecipe(duplicatedRecipe)
            onSuccess(newId)
        }
    }

    companion object {
        fun factory(repo: RecipeRepoInterface): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OwnedRecipeViewModel(repo) as T
            }
    }
}
