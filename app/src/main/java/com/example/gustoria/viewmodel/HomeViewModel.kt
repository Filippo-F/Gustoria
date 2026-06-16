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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val allRecipes: StateFlow<List<Recipe>> = SessionManagerFacade.currentUserId
        .flatMapLatest { _ ->
            recipeRepository.getAllRecipes()
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    val recommendedRecipes: StateFlow<List<Recipe>> = combine(
        allRecipes,
        SessionManagerFacade.currentUserId,
        _selectedCategory
    ) { recipes, userId, category ->
        recipes
            .filter { it.ownerId != (userId ?: "") }
            .filter { matchesCategory(it, category) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

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

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
    private fun matchesCategory(recipe: Recipe, category: String): Boolean =
        when (category) {
            "All"         -> true
            "Quick Meals" -> recipe.cookingTimeMinutes in 1..30
            "Vegan"       -> recipe.dietaryTags.any { it.equals("Vegan", true) }
            "Vegetarian"  -> recipe.dietaryTags.any { it.equals("Vegetarian", true) }
            "Gluten-Free" -> recipe.dietaryTags.any { it.equals("Gluten-Free", true) }
            "Italian"     -> recipe.cuisineType.equals("Italian", true)
            "Desserts"    -> recipe.mealType.equals("Dessert", true)
            else          -> true
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
