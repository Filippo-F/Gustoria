package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.HomeScreen
import com.example.gustoria.viewmodel.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
object Home

@Composable
fun HomeDestination(
    navActions: GustoriaNavigationActions,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val recommendedRecipes by viewModel.recommendedRecipes.collectAsStateWithLifecycle()
    val myRecipes by viewModel.myRecipes.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    HomeScreen(
        recommendedRecipes = recommendedRecipes,
        myRecipes = myRecipes,
        selectedCategory = selectedCategory,
        onNavigateToProfile = navActions::navigateToProfile,
        onNavigateToNotifications = navActions::navigateToNotifications,
        onNavigateToRecipeDetails = navActions::navigateToRecipeDetails,
        onCategorySelected = viewModel::selectCategory
    )
}
