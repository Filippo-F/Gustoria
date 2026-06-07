package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val recipeRepository: RecipeRepoInterface,
    private val userRepository: UserRepoInterface
) : ViewModel() {

    // All recipes
    @OptIn(ExperimentalCoroutinesApi::class)
    private val allRecipes: StateFlow<List<Recipe>> = SessionManagerFacade.currentUserId
        .flatMapLatest { _ ->
            recipeRepository.getAllRecipes()
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Recommended recipes
    val recommendedRecipes: StateFlow<List<Recipe>> = combine(
        allRecipes,
        SessionManagerFacade.currentUserId
    ) { recipes, userId ->
        recipes.filter { it.ownerId != (userId ?: "") }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // User's recipes
    @OptIn(ExperimentalCoroutinesApi::class)
    val myRecipes: StateFlow<List<Recipe>> = SessionManagerFacade.currentUserId
        .flatMapLatest { userId ->
            recipeRepository.getRecipeByOwner(userId ?: "")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Selected category
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val recipeRepository = application.container.recipeRepository
                val userRepository = application.container.userRepository
                HomeViewModel(recipeRepository, userRepository)
            }
        }
    }
}
