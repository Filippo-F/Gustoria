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
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val recipeRepository: RecipeRepoInterface,
    private val userRepository: UserRepoInterface
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentUser = SessionManagerFacade.currentUserId
        .flatMapLatest { userId ->
            if (userId == null) flowOf(null)
            else userRepository.getUserById(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val othersRecipes: StateFlow<List<Recipe>> = SessionManagerFacade.currentUserId
        .flatMapLatest { userId ->
            recipeRepository.getRecipesExcludingOwner(userId ?: "")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val recommendedRecipes: StateFlow<List<Recipe>> = combine(
        othersRecipes,
        currentUser
    ) { recipes, user ->
        if (user == null || (user.cuisinePreferences.isEmpty() && 
            user.dietaryRestrictions.isEmpty() && 
            user.favouriteMealTypes.isEmpty())) {
            recipes
        } else {
            val filtered = recipes.filter { recipe ->
                val matchesCuisine = user.cuisinePreferences.any { it.equals(recipe.cuisineType, ignoreCase = true) }
                val matchesDiet = user.dietaryRestrictions.any { pref ->
                    recipe.dietaryTags.any { tag -> tag.equals(pref, ignoreCase = true) }
                }
                val matchesMeal = user.favouriteMealTypes.any { it.equals(recipe.mealType, ignoreCase = true) }
                
                matchesCuisine || matchesDiet || matchesMeal
            }
            // If no matches found for preferences, show all other recipes as fallback
            if (filtered.isEmpty()) recipes else filtered
        }
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
