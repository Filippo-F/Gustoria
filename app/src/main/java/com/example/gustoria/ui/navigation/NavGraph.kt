package com.example.gustoria.ui.navigation

import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.gustoria.viewmodel.RecipeViewModel
import com.example.gustoria.viewmodel.ReviewViewModel
import com.example.gustoria.viewmodel.RecipeCollectionViewModel


class GustoriaNavigationActions(private val navController: NavController) {
    fun getNavController(): NavController = navController
    fun navigateBack() {
        navController.popBackStack()
    }

    private fun navigateWithTabHandling(route: Any) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
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
        navigateWithTabHandling(Home)
    }

    fun navigateToNotifications() {
        navigateWithTabHandling(Notifications)
    }

    fun navigateToAuthentication() {
        navController.navigate(Authentication)
    }

    fun navigateToActionRequirement(action: String) {
        navController.navigate(ActionRequirement(action))
    }

    fun navigateToSearch() {
        navigateWithTabHandling(Search)
    }

    fun navigateToSearching() {
        navController.navigate(Search.Searching)
    }

    fun navigateToSearched(recipeId: String? = null) {
        navController.navigate(Search.Searched(recipeId))
    }

    fun navigateToProfile() {
        navigateWithTabHandling(Profile)
    }

    fun navigateToOtherProfile(userId: String) {
        navController.navigate(OtherProfile(userId))
    }

    fun navigateToCreateRecipe() {
        navigateWithTabHandling(Create)
    }

    fun navigateToEditRecipe(id: String) {
        navController.navigate(Edit(id))
    }

    fun navigateToRecipeDetails(id: String) {
        navController.navigate(RecipeDetails(id))
    }

    fun navigateToFavouriteFiltering() {
        navigateWithTabHandling(Favourite.Filtering)
    }

    fun navigateToFavouriteSaved() {
        navigateWithTabHandling(Favourite)
    }

    fun navigateToFavouriteTried() {
        navigateWithTabHandling(Favourite.Tried)
    }

    fun navigateToFavouriteCreated() {
        navigateWithTabHandling(Favourite.Created)
    }

    fun navigateToReviewsList(recipeId: String) {
        navController.navigate(Review.ReviewsList(recipeId))
    }

    fun navigateToAddReview(recipeId: String) {
        navController.navigate(Review.AddReview(recipeId))
    }
}

@Composable
fun GustoriaApp(
    navController: NavHostController,
    navActions: GustoriaNavigationActions
) {
    Scaffold(
        bottomBar = {
            AppBottomNavBar(
                navCtrl = navController,
                navActions = navActions
            )
        },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { fadeIn(animationSpec = snap()) },
                exitTransition = { fadeOut(animationSpec = snap()) },
                popEnterTransition = { fadeIn(animationSpec = snap()) },
                popExitTransition = { fadeOut(animationSpec = snap()) }
            ){
                dialog<Authentication>(
                    // To have the dialog width not stuck at fixed size
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    AuthenticationDestination(
                        onAuthSuccess = { navActions.navigateBack() }
                    )
                }

                dialog<ActionRequirement>(
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
                ) { backStackEntry ->
                    val args: ActionRequirement = backStackEntry.toRoute()
                    ActionRequirementDestination(
                        action = args.action,
                        onAuthSuccess = { navActions.navigateBack() }
                    )
                }
                composable<Home> {
                    HomeDestination(navActions = navActions)
                }

                composable<Notifications> {
                    NotificationsDestination(navActions = navActions)
                }


                navigation<Search>(startDestination = Search.FeaturedSearch) {
                    composable<Search.FeaturedSearch> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Search>()
                        }
                        val recipeVm: RecipeViewModel = viewModel(
                            parentEntry,
                            factory = RecipeViewModel.Factory
                        )
                        FeaturedSearchDestination(
                            navActions = navActions,
                            recipeViewModel = recipeVm
                        )
                    }
                    composable<Search.Searching> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Search>()
                        }
                        val recipeVm: RecipeViewModel = viewModel(
                            parentEntry,
                            factory = RecipeViewModel.Factory
                        )
                        SearchingDestination(
                            navActions = navActions,
                            viewModel = recipeVm
                        )
                    }
                    composable<Search.Searched> { backStackEntry ->
                        val args: Search.Searched = backStackEntry.toRoute()
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Search>()
                        }
                        val recipeVm: RecipeViewModel = viewModel(
                            parentEntry,
                            factory = RecipeViewModel.Factory
                        )
                        SearchedDestination(
                            recipeId = args.recipeId,
                            navActions = navActions,
                            viewModel = recipeVm
                        )
                    }
                }

                composable<Create> {
                    CreateRecipeDestination(navActions = navActions)
                }

                composable<Edit> { backStackEntry ->
                    val edit: Edit = backStackEntry.toRoute()
                    EditRecipeDestination(
                        recipeId = edit.recipeId,
                        navActions = navActions
                    )
                }

                composable<RecipeDetails> { backStackEntry ->
                    val args: RecipeDetails = backStackEntry.toRoute()
                    RecipeDetailsDestination(
                        recipeId = args.recipeId,
                        navActions = navActions
                    )
                }

                navigation<Review>(startDestination = Review.ReviewsList("")) {
                    composable<Review.AddReview> { backStackEntry ->
                        val args: Review.AddReview = backStackEntry.toRoute()
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Review>()
                        }
                        val reviewVm: ReviewViewModel = viewModel(
                            parentEntry,
                            factory = ReviewViewModel.Factory
                        )
                        AddReviewDestination(
                            recipeId = args.recipeId,
                            navActions = navActions,
                            viewModel = reviewVm
                        )
                    }

                    composable<Review.ReviewsList> { backStackEntry ->
                        val args: Review.ReviewsList = backStackEntry.toRoute()
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Review>()
                        }
                        val reviewVm: ReviewViewModel = viewModel(
                            parentEntry,
                            factory = ReviewViewModel.Factory
                        )
                        ReviewsListDestination(
                            recipeId = args.recipeId,
                            navActions = navActions,
                            viewModel = reviewVm
                        )
                    }
                }

                navigation<Favourite>(startDestination = Favourite.Saved) {
                    composable<Favourite.Saved> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Favourite>()
                        }
                        val collectionVm: RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = RecipeCollectionViewModel.Factory
                        )
                        FavouriteSavedDestination(
                            navActions = navActions,
                            viewModel = collectionVm
                        )
                    }
                    composable<Favourite.Tried> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Favourite>()
                        }
                        val collectionVm: RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = RecipeCollectionViewModel.Factory
                        )
                        FavouriteTriedDestination(
                            navActions = navActions,
                            viewModel = collectionVm
                        )
                    }
                    composable<Favourite.Created> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Favourite>()
                        }
                        val collectionVm: RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = RecipeCollectionViewModel.Factory
                        )
                        FavouriteCreatedDestination(
                            navActions = navActions,
                            viewModel = collectionVm
                        )
                    }
                    composable<Favourite.Filtering> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Favourite>()
                        }
                        val collectionVm: RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = RecipeCollectionViewModel.Factory
                        )
                        FavouriteFilteringDestination(
                            navActions = navActions,
                            viewModel = collectionVm
                        )
                    }
                }

                navigation<Profile>(startDestination = Profile.OverallProfile) {
                    composable<Profile.OverallProfile> {
                        OverallProfileDestination(navActions)
                    }
                    composable<OtherProfile> { backStackEntry ->
                        val args: OtherProfile = backStackEntry.toRoute()
                        OtherProfileDestination(
                            userId = args.userId,
                            navActions = navActions
                        )
                    }
                    navigation<Profile.ProfileInfo>(startDestination = Profile.ProfileInfo.OverallProfileInfo) {
                        composable<Profile.ProfileInfo.OverallProfileInfo> {
                            ProfileInfoDestination(navActions)
                        }
                        dialog<Profile.ProfileInfo.CulinaryPreference> {
                            Text("Culinary Preference")
                        }
                        dialog<Profile.ProfileInfo.DietPreference> {
                            Text("Diet Preference")
                        }
                    }
                    composable<Profile.Settings> {
                        SettingsDestination(navActions)
                    }
                    dialog<Profile.HelpAndFeedback> {
                        HelpAndFeedbackDestination(navActions)
                    }
                    dialog<Profile.SignOut> {
                        SignOutDestination(navActions)
                    }
                }
            }
        }
    )
}

