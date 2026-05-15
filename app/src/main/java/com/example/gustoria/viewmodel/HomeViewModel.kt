package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.SessionManager
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val recipeRepository: RecipeRepoInterface,
    private val userRepository: UserRepoInterface
) : ViewModel() {

    // tutte le ricette (per featured)
    private val allRecipes: StateFlow<List<Recipe>> = recipeRepository.getAllRecipes()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // prima ricetta non posseduta dall'utente corrente (suggerita nella Home)
    val featuredRecipe: StateFlow<Recipe?> = allRecipes
        .map { list -> list.firstOrNull { it.ownerId != SessionManager.CURRENT_LOGGED_IN_USER_ID } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    // ricette create dall'utente corrente (sezione "Recent Creations")
    val myRecipes: StateFlow<List<Recipe>> = recipeRepository
        .getRecipeByOwner(SessionManager.CURRENT_LOGGED_IN_USER_ID)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // categoria selezionata (sezione "Explore Categories")
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    companion object {
        fun provideFactory(
            recipeRepository: RecipeRepoInterface,
            userRepository: UserRepoInterface
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HomeViewModel(recipeRepository, userRepository) as T
            }
    }
}
