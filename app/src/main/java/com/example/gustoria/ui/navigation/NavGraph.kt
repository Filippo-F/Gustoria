package com.example.gustoria.ui.navigation

import androidx.navigation.NavController
import com.example.gustoria.ui.navigation.Favourite
import com.example.gustoria.ui.navigation.Review
import com.example.gustoria.ui.navigation.Search

class GustoriaNavigationActions(private val navController: NavController) {
    fun getNavController(): NavController = navController
    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToOverallProfileInfo() {
        navController.navigate(Profile.ProfileInfo.OverallProfileInfo)
    }

    fun navigateToSettings() {
        navController.navigate(Profile.Settings)
    }

    fun navigateToHelpAndFeedback() {
        navController.navigate(Profile.HelpAndFeedback)
    }

    fun navigateToSignOut() {
        navController.navigate(Profile.SignOut)
    }

    fun navigateToCulinaryPreference() {
        navController.navigate(Profile.ProfileInfo.CulinaryPreference)
    }

    fun navigateToDietPreference() {
        navController.navigate(Profile.ProfileInfo.DietPreference)
    }

    fun navigateToHome() {
        navController.navigate(Home)
    }

    fun navigateToNotifications() {
        navController.navigate(Notifications)
    }

    fun navigateToAuthentication() {
        navController.navigate(Authentication)
    }

    fun navigateToSearch() {
        navController.navigate(Search.FeaturedSearch)
    }

    fun navigateToSearching() {
        navController.navigate(Search.Searching)
    }

    fun navigateToSearched(recipeId: String? = null) {
        navController.navigate(Search.Searched(recipeId))
    }

    fun navigateToProfile() {
        navController.navigate(com.example.gustoria.ui.navigation.Profile.OverallProfile)
    }

    fun navigateToOtherProfile(userId: String) {
        navController.navigate(OtherProfile(userId))
    }

    fun navigateToCreateRecipe() {
        navController.navigate(Create)
    }

    fun navigateToEditRecipe(id: String) {
        navController.navigate(Edit(id))
    }

    fun navigateToRecipeDetails(id: String) {
        navController.navigate(RecipeDetails(id))
    }

    fun navigateToFavouriteFiltering() {
        navController.navigate(Favourite.Filtering) { launchSingleTop = true }
    }

    fun navigateToFavouriteSaved() {
        navController.navigate(Favourite.Saved) { launchSingleTop = true; restoreState = true }
    }

    fun navigateToFavouriteTried() {
        navController.navigate(Favourite.Tried) { launchSingleTop = true; restoreState = true }
    }

    fun navigateToFavouriteCreated() {
        navController.navigate(Favourite.Created) { launchSingleTop = true; restoreState = true }
    }

    fun navigateToReviewsList(recipeId: String) {
        navController.navigate(Review.ReviewsList(recipeId))
    }

    fun navigateToAddReview(recipeId: String) {
        navController.navigate(Review.AddReview(recipeId))
    }
}
