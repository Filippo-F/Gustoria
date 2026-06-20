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
    data class Searched(val recipeId: String? = null, val initialFilter: String? = null)
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
            recipeViewModel.toggleCuisine(categoryName)
            navActions.navigateToSearched()
        },
        onRecentSearchClick = { recentSearch ->
            recipeViewModel.setFilters(recentSearch.filters)
            searchViewModel.addRecentSearch(recentSearch.filters)
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
            searchViewModel.addRecentSearch(filters)
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
    initialFilter: String? = null,
    navActions: GustoriaNavigationActions,
    viewModel: RecipeViewModel
) {
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    LaunchedEffect(recipeId, initialFilter) {
        if (recipeId != null) {
            navActions.navigateToRecipeDetails(recipeId)
        }
        if (initialFilter != null) {
            viewModel.resetFilters()
            viewModel.toggleMealType(initialFilter)
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
