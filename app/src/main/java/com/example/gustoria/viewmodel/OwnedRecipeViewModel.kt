package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.SessionManager
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class OwnedRecipeViewModel(
    private val repo: RecipeRepoInterface
) : ViewModel() {

    private val userId = SessionManager.CURRENT_LOGGED_IN_USER_ID

    val nameQuery = MutableStateFlow("")
    val ingredientQuery = MutableStateFlow("")
    val costs = MutableStateFlow<Set<String>>(emptySet())
    val difficulties = MutableStateFlow<Set<String>>(emptySet())

    private val myRecipes: StateFlow<List<Recipe>> =
        repo.getRecipeByOwner(userId)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val recipesToShow: StateFlow<List<Recipe>> = combine(
        myRecipes, nameQuery, ingredientQuery, costs, difficulties
    ) { list, name, ing, c, d ->
        val result = mutableListOf<Recipe>()
        for (recipe in list) {
            if (matches(recipe, name, ing, c, d)) {
                result.add(recipe)
            }
        }
        result
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe

    fun selectRecipe(recipe: Recipe?) {
        _selectedRecipe.value = recipe
    }

    private fun matches(
        r: Recipe,
        name: String,
        ing: String,
        costs: Set<String>,
        diffs: Set<String>
    ): Boolean {
        if (name.isNotBlank()) {
            if (!r.name.contains(name, ignoreCase = true)) return false
        }
        if (ing.isNotBlank()) {
            var found = false
            for (i in r.ingredients) {
                if (i.name.contains(ing, ignoreCase = true)) {
                    found = true
                    break
                }
            }
            if (!found) return false
        }
        if (costs.isNotEmpty()) {
            if (r.cost !in costs) return false
        }
        if (diffs.isNotEmpty()) {
            if (r.difficulty !in diffs) return false
        }
        return true
    }

    fun setNameQuery(q: String) {
        nameQuery.value = q
    }

    fun setIngredientQuery(q: String) {
        ingredientQuery.value = q
    }

    fun toggleCost(c: String) {
        val current = costs.value
        if (c in current) {
            costs.value = current - c
        } else {
            costs.value = current + c
        }
    }

    fun toggleDifficulty(d: String) {
        val current = difficulties.value
        if (d in current) {
            difficulties.value = current - d
        } else {
            difficulties.value = current + d
        }
    }

    fun resetFilters() {
        nameQuery.value = ""
        ingredientQuery.value = ""
        costs.value = emptySet()
        difficulties.value = emptySet()
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.deleteRecipe(id)
            if (_selectedRecipe.value?.id == id) {
                _selectedRecipe.value = null
            }
        }
    }

    fun duplicateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val duplicatedRecipe = recipe.copy(
                id = java.util.UUID.randomUUID().toString(),
                ownerId = userId,
                name = if (recipe.name.endsWith(" (Copy)")) recipe.name else "${recipe.name} (Copy)"
            )
            repo.addRecipe(duplicatedRecipe)
        }
    }

    //da rivedere campanion object
    companion object {
        fun factory(repo: RecipeRepoInterface): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OwnedRecipeViewModel(repo) as T
            }
    }
}
