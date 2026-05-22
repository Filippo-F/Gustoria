package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gustoria.ui.theme.GustoriaTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import com.example.gustoria.ui.navigation.AppBottomNavBar
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.example.gustoria.ui.HomeScreen
import com.example.gustoria.ui.authentication.AuthenticationDialogue
import com.example.gustoria.ui.recipe.EditRecipeScreen
import com.example.gustoria.ui.recipe.RecipeDetailsScreen
import com.example.gustoria.ui.recipe.RecipeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.SearchingScreen
import com.example.gustoria.ui.navigation.AddReviewDestination
import com.example.gustoria.ui.navigation.Authentication
import com.example.gustoria.ui.navigation.AuthenticationDestination
import com.example.gustoria.ui.navigation.Favourite
import com.example.gustoria.ui.navigation.FavouriteCreatedDestination
import com.example.gustoria.ui.navigation.FavouriteFilteringDestination
import com.example.gustoria.ui.navigation.FavouriteSavedDestination
import com.example.gustoria.ui.navigation.FavouriteTriedDestination
import com.example.gustoria.ui.navigation.FeaturedSearchDestination
import com.example.gustoria.ui.navigation.GustoriaNavigationActions
import com.example.gustoria.ui.navigation.Home
import com.example.gustoria.ui.navigation.HomeDestination
import com.example.gustoria.ui.navigation.Notifications
import com.example.gustoria.ui.navigation.NotificationsDestination
import com.example.gustoria.ui.navigation.OverallProfileDestination
import com.example.gustoria.ui.navigation.Profile
import com.example.gustoria.ui.navigation.ProfileInfoDestination
import com.example.gustoria.ui.navigation.Review
import com.example.gustoria.ui.navigation.ReviewsListDestination
import com.example.gustoria.ui.navigation.Search
import com.example.gustoria.ui.navigation.SearchedDestination
import com.example.gustoria.ui.navigation.SearchingDestination
import com.example.gustoria.ui.navigation.SettingsDestination
import com.example.gustoria.ui.navigation.HelpAndFeedbackDestination
import com.example.gustoria.ui.navigation.SignOutDestination
import com.example.gustoria.ui.navigation.OtherProfile
import com.example.gustoria.ui.navigation.OtherProfileDestination
import com.example.gustoria.ui.notifications.NotificationScreen
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GustoriaTheme {
                val navController = rememberNavController() // Create the NavController
                val navActions = remember(navController) {
                    GustoriaNavigationActions(navController)
                } // Create the Navigation Actions, passing the NavController
                GustoriaApp(navController, navActions)
            }
        }
    }
}


@Serializable
object Create

@Serializable
data class Edit(val recipeId: String)
@Serializable
data class RecipeDetails(val recipeId: String)

@Composable
fun GustoriaApp(
    navController: NavHostController,
    navActions: GustoriaNavigationActions
) {
    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                AppBottomNavBar(
                    navCtrl = navController,
                    navActions = navActions
                )
            }
        },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier.padding(paddingValues)
            ){
                dialog<Authentication>(
                    // To have the dialog width not stuck at fixed size
                    dialogProperties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    AuthenticationDestination(
                        onAuthSuccess = {}
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
                        val recipeVm: com.example.gustoria.viewmodel.RecipeViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeViewModel.Factory
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
                        val recipeVm: com.example.gustoria.viewmodel.RecipeViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeViewModel.Factory
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
                        val recipeVm: com.example.gustoria.viewmodel.RecipeViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeViewModel.Factory
                        )
                        SearchedDestination(
                            recipeId = args.recipeId,
                            navActions = navActions,
                            viewModel = recipeVm
                        )
                    }
                }

                composable<Create> {
                    EditRecipeScreen(
                        navController = navController,
                        recipeId = null
                    )
                }

                composable<Edit> { backStackEntry ->
                    val edit: Edit = backStackEntry.toRoute()
                    EditRecipeScreen(
                        navController = navController,
                        recipeId = edit.recipeId
                    )
                }

                composable<RecipeDetails> { backStackEntry ->
                    val args: RecipeDetails = backStackEntry.toRoute()
                    RecipeDetailsScreen(
                        recipeId = args.recipeId,
                        navCtrl = navController,
                        navActions = navActions
                    )
                }

                navigation<Review>(startDestination = Review.ReviewsList("")) {
                    composable<Review.AddReview> { backStackEntry ->
                        val args: Review.AddReview = backStackEntry.toRoute()
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Review>()
                        }
                        val reviewVm: com.example.gustoria.viewmodel.ReviewViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.ReviewViewModel.Factory
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
                        val reviewVm: com.example.gustoria.viewmodel.ReviewViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.ReviewViewModel.Factory
                        )
                        ReviewsListDestination(
                            recipeId = args.recipeId,
                            navActions = navActions,
                            viewModel = reviewVm
                        )
                    }
                }

                composable<OtherProfile> { backStackEntry ->
                    val args: OtherProfile = backStackEntry.toRoute()
                    OtherProfileDestination(
                        userId = args.userId,
                        navActions = navActions
                    )
                }

                navigation<Favourite>(startDestination = Favourite.Saved) {
                    composable<Favourite.Saved> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Favourite>()
                        }
                        val collectionVm: com.example.gustoria.viewmodel.RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeCollectionViewModel.Factory
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
                        val collectionVm: com.example.gustoria.viewmodel.RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeCollectionViewModel.Factory
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
                        val collectionVm: com.example.gustoria.viewmodel.RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeCollectionViewModel.Factory
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
                        val collectionVm: com.example.gustoria.viewmodel.RecipeCollectionViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeCollectionViewModel.Factory
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
