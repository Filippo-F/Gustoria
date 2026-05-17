package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import com.example.gustoria.dataclass.Recipe
import com.example.gustoria.SessionManager
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.ui.recipe.RecipeFilters
import com.example.gustoria.ui.recipe.applyFilters
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalUuidApi::class)
class RecipeCollectionViewModel(
    private val repo: RecipeRepoInterface,
    private val userRepo: UserRepoInterface
) : ViewModel() {

    private val userId = SessionManager.CURRENT_LOGGED_IN_USER_ID

    private val _filters = MutableStateFlow(RecipeFilters())
    val filters: StateFlow<RecipeFilters> = _filters.asStateFlow()
    // 1. Mocked Saved Recipes (Medium Difficulty)
    private val savedRecipes: StateFlow<List<Recipe>> = combine(
        repo.getAllRecipes(),
        userRepo.getFavouriteRecipeIds(userId)
    ) { allRecipes, favouriteIds ->
        allRecipes.filter { it.id in favouriteIds }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 2. Mocked Tried Recipes (Easy Difficulty)
    private val triedRecipes: StateFlow<List<Recipe>> = repo.getAllRecipes()
        .map { list -> list.filter { it.difficulty == "Easy" } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // 3. Created Recipes (Owned by User)
    private val createdRecipes: StateFlow<List<Recipe>> = repo.getRecipeByOwner(userId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Keep track of current tab (tab corrente) (0: Saved, 1: Tried, 2: Created)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _selectedRecipeId = MutableStateFlow<String?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipeId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repo.getRecipeById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setTab(index: Int) {
        _currentTab.value = index
        resetFilters()
    }

    // This dynamically changes the list based on the active tab and applies your existing filters!
    val recipesToShow: StateFlow<List<Recipe>> = combine(
        savedRecipes, triedRecipes, createdRecipes, _currentTab, _filters
    ) { saved, tried, created, tabIndex, f ->
        val baseList = when (tabIndex) {
            0 -> saved
            1 -> tried
            else -> created
        }
        baseList.applyFilters(f)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setNameQuery(q: String) { _filters.update { it.copy(nameQuery = q) } }
    fun setIngredientQuery(q: String) { _filters.update { it.copy(ingredientQuery = q) } }
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
    fun resetFilters() { _filters.value = RecipeFilters() }

    fun selectRecipe(recipeId: String?) {
        _selectedRecipeId.value = recipeId
    }

    fun isOwnedByCurrentUser(recipe: Recipe): Boolean {
        return recipe.ownerId == userId
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.deleteRecipe(id)
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

    fun isFavouriteFlow(recipeId: String): Flow<Boolean> =
        userRepo.isFavourite(userId, recipeId)
    fun toggleFavourite(recipeId: String) {
        viewModelScope.launch {
            val isFav = userRepo
                .isFavourite(userId, recipeId)
                .first()  // legge il valore corrente una volta
            if (isFav) userRepo.removeFavourite(userId, recipeId)
            else userRepo.addFavourite(userId, recipeId)
        }
    }
    companion object {
        fun factory(
            repo: RecipeRepoInterface,
            userRepo: UserRepoInterface
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RecipeCollectionViewModel(repo, userRepo) as T
        }
    }
}