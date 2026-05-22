package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.gustoria.ui.theme.GustoriaTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import com.example.gustoria.ui.AppBottomNavBar
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
import com.example.gustoria.ui.user.HelpAndFeedbackDialogue
import com.example.gustoria.ui.user.OwnedProfileScreen
import com.example.gustoria.ui.user.SignOutDialogue
import com.example.gustoria.ui.user.SettingsScreen
import com.example.gustoria.ui.user.ProfileInfoScreen
import com.example.gustoria.ui.user.OtherProfileScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.SearchingScreen
import com.example.gustoria.ui.navigation.GustoriaNavigationActions
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
object Home

@Serializable
object Notifications

@Serializable
object Search {
    @Serializable
    object FeaturedSearch
    @Serializable
    object Searching
    @Serializable
    data class Searched(val recipeId: String? = null)
}

@Serializable
object Create

@Serializable
data class Edit(val recipeId: String)
@Serializable
data class RecipeDetails(val recipeId: String)
@Serializable
data class AddReview(val recipeId: String)
@Serializable
data class ReviewsList(val recipeId: String)
@Serializable
data class OtherProfile(val userId: String)

@Serializable
object Profile {
    @Serializable
    object OverallProfile
    @Serializable
    object ProfileInfo {
        @Serializable
        object OverallProfileInfo
        @Serializable
        object CulinaryPreference
        @Serializable
        object DietPreference
    }
    @Serializable
    object Settings
    @Serializable
    object HelpAndFeedback
    @Serializable
    object SignOut
}
@Serializable
object Authentication

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
fun GustoriaApp(
    navController: NavHostController,
    navActions: GustoriaNavigationActions
) {
    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                AppBottomNavBar(
                    navCtrl = navController
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
                    AuthenticationDialogue(
                        onAuthSuccess = {},
                    )

                }
                composable<Home> {
                    HomeScreen(
                        navCtrl = navController
                    )
                }

                composable<Notifications> {
                    NotificationScreen(navController = navController)
                }


                navigation<Search>(startDestination = Search.FeaturedSearch) {
                    composable<Search.FeaturedSearch> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Search>()
                        }
                        val searchVm: com.example.gustoria.viewmodel.RecipeViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeViewModel.Factory
                        )

                        com.example.gustoria.ui.FeaturedSearchScreen(
                            recipeVm = searchVm,
                            onSearchClick = {
                                navController.navigate(Search.Searching)
                            },
                            onCategoryClick = { categoryName ->
                                searchVm.resetFilters()
                                searchVm.updateNameQuery(categoryName)
                                navController.navigate(Search.Searched())
                            },
                            onRecentSearchClick = { query ->
                                searchVm.resetFilters()
                                searchVm.updateNameQuery(query)
                                navController.navigate(Search.Searched())
                            },
                            onTrendingTagClick = { tag ->
                                searchVm.resetFilters()
                                searchVm.updateNameQuery(tag.removePrefix("#"))
                                navController.navigate(Search.Searched())
                            }
                        )
                    }
                    composable<Search.Searching> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Search>()
                        }
                        val searchVm: com.example.gustoria.viewmodel.RecipeViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeViewModel.Factory
                        )
                        val filters by searchVm.filters.collectAsStateWithLifecycle()
                        val recipes by searchVm.filteredRecipes.collectAsStateWithLifecycle()

                        SearchingScreen(
                            filters = filters,
                            resultCount = recipes.size,
                            onClose = { navController.popBackStack() },
                            onShowResultsClick = { navController.navigate(Search.Searched()) },
                            onResetFilters = searchVm::resetFilters,
                            onToggleDifficulty = searchVm::toggleDifficulty,
                            onToggleCost = searchVm::toggleCost,
                            onNameQueryChange = searchVm::updateNameQuery,
                            onIngredientQueryChange = searchVm::updateIngredientQuery
                        )
                    }
                    composable<Search.Searched> { backStackEntry ->
                        val args: Search.Searched = backStackEntry.toRoute()
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<Search>()
                        }
                        val searchVm: com.example.gustoria.viewmodel.RecipeViewModel = viewModel(
                            parentEntry,
                            factory = com.example.gustoria.viewmodel.RecipeViewModel.Factory
                        )

                        RecipeScreen(
                            navCtrl = navController,
                            initialRecipeId = args.recipeId,
                            viewModel = searchVm
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
                        navCtrl = navController
                    )
                }

                composable<AddReview> { backStackEntry ->
                    val args: AddReview = backStackEntry.toRoute()
                    com.example.gustoria.ui.review.ReviewFormScreen(
                        recipeId = args.recipeId,
                        navController = navController
                    )
                }

                composable<ReviewsList> { backStackEntry ->
                    val args: ReviewsList = backStackEntry.toRoute()
                    com.example.gustoria.ui.review.ReviewsListScreen(
                        recipeId = args.recipeId,
                        navController = navController
                    )
                }

                composable<OtherProfile> { backStackEntry ->
                    val args: OtherProfile = backStackEntry.toRoute()
                    OtherProfileScreen(
                        viewedUserId = args.userId,
                        onBack = { navController.popBackStack() }
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
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            initialTab = 0,
                            navController = navController,
                            vm = collectionVm
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
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            initialTab = 1,
                            navController = navController,
                            vm = collectionVm
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
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            initialTab = 2,
                            navController = navController,
                            vm = collectionVm
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
                        val filters by collectionVm.filters.collectAsStateWithLifecycle()
                        val recipes by collectionVm.recipesToShow.collectAsStateWithLifecycle()

                        SearchingScreen(
                            filters = filters,
                            resultCount = recipes.size,
                            onClose = { navController.popBackStack() },
                            onShowResultsClick = { navController.popBackStack() }, // Goes back to list
                            onResetFilters = collectionVm::resetFilters,
                            onToggleDifficulty = collectionVm::toggleDifficulty,
                            onToggleCost = collectionVm::toggleCost,
                            onNameQueryChange = collectionVm::setNameQuery,
                            onIngredientQueryChange = collectionVm::setIngredientQuery
                        )
                    }
                }

                navigation<Profile>(startDestination = Profile.OverallProfile) {
                    composable<Profile.OverallProfile> {
                        OwnedProfileScreen(
                            navController = navController
                        )
                    }
                    navigation<Profile.ProfileInfo>(startDestination = Profile.ProfileInfo.OverallProfileInfo) {
                        composable<Profile.ProfileInfo.OverallProfileInfo> {
                            ProfileInfoScreen(
                                navController = navController
                            )
                        }
                        dialog<Profile.ProfileInfo.CulinaryPreference> {
                            Text("Culinary Preference")
                        }
                        dialog<Profile.ProfileInfo.DietPreference> {
                            Text("Diet Preference")
                        }
                    }
                    composable<Profile.Settings> {
                        SettingsScreen(navController = navController)
                    }
                    dialog<Profile.HelpAndFeedback> {
                        HelpAndFeedbackDialogue(navController = navController)
                    }
                    dialog<Profile.SignOut> {
                        SignOutDialogue(navController = navController)
                    }
                }
            }
        }
    )
}
