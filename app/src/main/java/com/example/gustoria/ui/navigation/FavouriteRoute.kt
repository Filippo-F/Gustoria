package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gustoria.ui.SearchingScreen
import com.example.gustoria.ui.recipe.RecipeCollectionScreen
import com.example.gustoria.viewmodel.RecipeCollectionViewModel
import kotlinx.serialization.Serializable

@Serializable
object Favourite {
    @Serializable
    object Saved
    @Serializable
    object Tried
    @Serializable
    object Created
    @Serializable
    object Filtering
}

@Composable
fun FavouriteSavedDestination(
    navActions: GustoriaNavigationActions,
    viewModel: RecipeCollectionViewModel
) {
    val recipes by viewModel.recipesToShow.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setTab(0)
    }

    RecipeCollectionScreen(
        recipes = recipes,
        filters = filters,
        currentTab = currentTab,
        onTabSelected = { index ->
            viewModel.setTab(index)
            when (index) {
                0 -> navActions.navigateToFavouriteSaved()
                1 -> navActions.navigateToFavouriteTried()
                2 -> navActions.navigateToFavouriteCreated()
            }
        },
        onCreateNewRecipe = navActions::navigateToCreateRecipe,
        onEditRecipe = navActions::navigateToEditRecipe,
        onRecipeClick = navActions::navigateToRecipeDetails,
        onDeleteRecipe = viewModel::delete,
        onNameQueryChange = viewModel::setNameQuery,
        onIngredientQueryChange = viewModel::setIngredientQuery,
        onToggleCost = viewModel::toggleCost,
        onToggleDifficulty = viewModel::toggleDifficulty,
        onOpenFilters = navActions::navigateToFavouriteFiltering
    )
}

@Composable
fun FavouriteTriedDestination(
    navActions: GustoriaNavigationActions,
    viewModel: RecipeCollectionViewModel
) {
    val recipes by viewModel.recipesToShow.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setTab(1)
    }

    RecipeCollectionScreen(
        recipes = recipes,
        filters = filters,
        currentTab = currentTab,
        onTabSelected = { index ->
            viewModel.setTab(index)
            when (index) {
                0 -> navActions.navigateToFavouriteSaved()
                1 -> navActions.navigateToFavouriteTried()
                2 -> navActions.navigateToFavouriteCreated()
            }
        },
        onCreateNewRecipe = navActions::navigateToCreateRecipe,
        onEditRecipe = navActions::navigateToEditRecipe,
        onRecipeClick = navActions::navigateToRecipeDetails,
        onDeleteRecipe = viewModel::delete,
        onNameQueryChange = viewModel::setNameQuery,
        onIngredientQueryChange = viewModel::setIngredientQuery,
        onToggleCost = viewModel::toggleCost,
        onToggleDifficulty = viewModel::toggleDifficulty,
        onOpenFilters = navActions::navigateToFavouriteFiltering
    )
}

@Composable
fun FavouriteCreatedDestination(
    navActions: GustoriaNavigationActions,
    viewModel: RecipeCollectionViewModel
) {
    val recipes by viewModel.recipesToShow.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setTab(2)
    }

    RecipeCollectionScreen(
        recipes = recipes,
        filters = filters,
        currentTab = currentTab,
        onTabSelected = { index ->
            viewModel.setTab(index)
            when (index) {
                0 -> navActions.navigateToFavouriteSaved()
                1 -> navActions.navigateToFavouriteTried()
                2 -> navActions.navigateToFavouriteCreated()
            }
        },
        onCreateNewRecipe = navActions::navigateToCreateRecipe,
        onEditRecipe = navActions::navigateToEditRecipe,
        onRecipeClick = navActions::navigateToRecipeDetails,
        onDeleteRecipe = viewModel::delete,
        onNameQueryChange = viewModel::setNameQuery,
        onIngredientQueryChange = viewModel::setIngredientQuery,
        onToggleCost = viewModel::toggleCost,
        onToggleDifficulty = viewModel::toggleDifficulty,
        onOpenFilters = navActions::navigateToFavouriteFiltering
    )
}

@Composable
fun FavouriteFilteringDestination(
    navActions: GustoriaNavigationActions,
    viewModel: RecipeCollectionViewModel
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val recipes by viewModel.recipesToShow.collectAsStateWithLifecycle()

    SearchingScreen(
        filters = filters,
        resultCount = recipes.size,
        onClose = navActions::navigateBack,
        onShowResultsClick = navActions::navigateBack,
        onResetFilters = viewModel::resetFilters,
        onToggleDifficulty = viewModel::toggleDifficulty,
        onToggleCost = viewModel::toggleCost,
        onNameQueryChange = viewModel::setNameQuery,
        onIngredientQueryChange = viewModel::setIngredientQuery
    )
}
