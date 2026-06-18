package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gustoria.ui.FeaturedSearchScreen
import com.example.gustoria.ui.SearchingScreen
import com.example.gustoria.ui.recipe.RecipeScreen
import com.example.gustoria.viewmodel.RecipeViewModel
import com.example.gustoria.viewmodel.SearchViewModel
import kotlinx.serialization.Serializable

@Serializable
object Search {
    @Serializable
    object FeaturedSearch
    @Serializable
    object Searching
    @Serializable
    data class Searched(val recipeId: String? = null)
}

@Composable
fun FeaturedSearchDestination(
    navActions: GustoriaNavigationActions,
    recipeViewModel: RecipeViewModel,
    searchViewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = SearchViewModel.Factory)
) {
    val recentSearches by searchViewModel.recentSearches.collectAsStateWithLifecycle()

    FeaturedSearchScreen(
        recentSearches = recentSearches,
        onSearchClick = navActions::navigateToSearching,
        onCategoryClick = { categoryName ->
            recipeViewModel.resetFilters()
            recipeViewModel.updateNameQuery(categoryName)
            searchViewModel.addRecentSearch(categoryName)
            navActions.navigateToSearched()
        },
        onRecentSearchClick = { query ->
            recipeViewModel.resetFilters()
            recipeViewModel.updateNameQuery(query)
            searchViewModel.addRecentSearch(query)
            navActions.navigateToSearched()
        },
        onClearAllRecentSearches = searchViewModel::clearAllRecentSearches,
        onRemoveRecentSearch = searchViewModel::removeRecentSearch
    )
}

@Composable
fun SearchingDestination(
    navActions: GustoriaNavigationActions,
    viewModel: RecipeViewModel,
    searchViewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = SearchViewModel.Factory)
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val recipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()

    SearchingScreen(
        filters = filters,
        resultCount = recipes.size,
        onClose = navActions::navigateBack,
        onShowResultsClick = {
            val query = filters.nameQuery.trim()
            if (query.isNotBlank()) {
                searchViewModel.addRecentSearch(query)
            }
            navActions.navigateToSearched()
        },
        onResetFilters = viewModel::resetFilters,
        onToggleDifficulty = viewModel::toggleDifficulty,
        onToggleCost = viewModel::toggleCost,
        onToggleCuisine = viewModel::toggleCuisine,
        onToggleMealType = viewModel::toggleMealType,
        onToggleDietaryTag = viewModel::toggleDietaryTag,
        onSetMaxCookingTime = viewModel::setMaxCookingTime,
        onNameQueryChange = viewModel::updateNameQuery,
        onIngredientQueryChange = viewModel::updateIngredientQuery
    )
}

@Composable
fun SearchedDestination(
    recipeId: String?,
    navActions: GustoriaNavigationActions,
    viewModel: RecipeViewModel
) {
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            navActions.navigateToRecipeDetails(recipeId)
        }
    }

    RecipeScreen(
        recipes = filteredRecipes,
        filters = filters,
        onBack = navActions::navigateBack,
        onAdjustFilters = navActions::navigateToSearching,
        onRecipeClick = navActions::navigateToRecipeDetails,
        onRemoveFilter = { filterLabel ->
            when {
                filterLabel in filters.selectedCosts         -> viewModel.toggleCost(filterLabel)
                filterLabel in filters.selectedDifficulties  -> viewModel.toggleDifficulty(filterLabel)
                filterLabel in filters.selectedCuisines      -> viewModel.toggleCuisine(filterLabel)
                filterLabel in filters.selectedMealTypes     -> viewModel.toggleMealType(filterLabel)
                filterLabel in filters.selectedDietaryTags   -> viewModel.toggleDietaryTag(filterLabel)
                filterLabel == filters.nameQuery             -> viewModel.updateNameQuery("")
                filterLabel == filters.ingredientQuery       -> viewModel.updateIngredientQuery("")
                filterLabel.startsWith("≤") && filterLabel.endsWith("min") -> viewModel.setMaxCookingTime(null)
            }
        }
    )
}
