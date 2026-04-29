package com.example.gustoria.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.Dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val CURRENT_LOGGED_IN_USER_ID = "101"

class RecipeViewModel(
    private val recipeRepository: RecipeRepoInterface // TODO: da verificare
) : ViewModel() {

    val recipes: StateFlow<List<Recipe>> = recipeRepository.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedRecipeId = MutableStateFlow<String?>(null)

    val selectedRecipe: StateFlow<Recipe?> =
        combine(recipes, _selectedRecipeId) { recipeList, id ->
            recipeList.find { it.id == id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun selectRecipe(recipeId: String?) {
        _selectedRecipeId.value = recipeId
    }

    fun deleteRecipe(recipeId: String) { // attraverso il repository
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipeId)
            selectRecipe(null)
        }
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.addRecipe(
                recipe.copy(ownerId = CURRENT_LOGGED_IN_USER_ID)
            )
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.updateRecipe(recipe.id, recipe)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun duplicateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val copy = recipe.copy(
                id = Uuid.random().toString(), // cambio ID
                ownerId = CURRENT_LOGGED_IN_USER_ID, // cambio owner
                name = "${recipe.name} Copy"
            )
            recipeRepository.addRecipe(copy)
        }
    }

    companion object {
        fun provideFactory(
            recipeRepository: RecipeRepoInterface // TODO: da verificare
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecipeViewModel(recipeRepository) as T
                }
            }
    }
}