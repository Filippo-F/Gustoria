package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.model.PaperRecipeRepo
import com.example.gustoria.ui.theme.GustoriaTheme
import io.paperdb.Paper
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import com.example.gustoria.ui.AppBottomNavBar
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.model.PaperReviewRepo
import com.example.gustoria.model.PaperUserRepo
import androidx.navigation.toRoute
import com.example.gustoria.ui.HomeScreen
import com.example.gustoria.ui.authentication.AuthenticationDialogue
import com.example.gustoria.ui.recipe.EditRecipeScreen
import com.example.gustoria.ui.SearchingScreen
import com.example.gustoria.ui.user.OwnedProfileScreen
import com.example.gustoria.ui.user.OtherProfileScreen
import com.example.gustoria.viewmodel.AuthMode
import com.example.gustoria.viewmodel.RecipeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gustoria.ui.recipe.RecipeDetailsScreen
import com.example.gustoria.viewmodel.RecipeCollectionViewModel

class MainActivity : ComponentActivity() {
    private lateinit var recipeRepository: RecipeRepoInterface
    private lateinit var userRepository: UserRepoInterface
    private lateinit var reviewRepository: ReviewRepoInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Paper.init(applicationContext)
        recipeRepository = PaperRecipeRepo()
        userRepository = PaperUserRepo()
        reviewRepository = PaperReviewRepo()

        setContent {
            GustoriaTheme {
                GustoriaApp(
                    recipeRepository = recipeRepository,
                    userRepository = userRepository,
                    reviewRepository = reviewRepository
                )
            }
        }
    }
}


@Serializable
object Home

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
    recipeRepository: RecipeRepoInterface,
    userRepository: UserRepoInterface,
    reviewRepository: ReviewRepoInterface
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val sharedSearchRecipeViewModel: com.example.gustoria.viewmodel.RecipeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.example.gustoria.viewmodel.RecipeViewModel.provideFactory(recipeRepository, userRepository)
    )
    val sharedCollectionViewModel: com.example.gustoria.viewmodel.RecipeCollectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.example.gustoria.viewmodel.RecipeCollectionViewModel.factory(recipeRepository, userRepository)
    )

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
                        userRepo = userRepository,
                        onAuthSuccess = {},
                    )

                }
                composable<Home> {
                    HomeScreen(
                        navCtrl = navController,
                        recipeRepository = recipeRepository,
                        userRepository = userRepository
                    )
                }


                navigation<Search>(startDestination = Search.FeaturedSearch) {
                    composable<Search.FeaturedSearch> {
                        com.example.gustoria.ui.FeaturedSearchScreen(
                            recipeVm = sharedSearchRecipeViewModel,   // VM condiviso
                            onSearchClick = {
                                navController.navigate(Search.Searching)
                            },
                            onCategoryClick = { categoryName ->
                                sharedSearchRecipeViewModel.resetFilters()
                                sharedSearchRecipeViewModel.updateNameQuery(categoryName)
                                navController.navigate(Search.Searched())
                            },
                            onRecentSearchClick = { query ->
                                sharedSearchRecipeViewModel.resetFilters()
                                sharedSearchRecipeViewModel.updateNameQuery(query)
                                navController.navigate(Search.Searched())
                            },
                            onTrendingTagClick = { tag ->
                                sharedSearchRecipeViewModel.resetFilters()
                                sharedSearchRecipeViewModel.updateNameQuery(tag.removePrefix("#"))
                                navController.navigate(Search.Searched())
                            }
                        )
                    }
                    composable<Search.Searching> {
                        val filters by sharedSearchRecipeViewModel.filters.collectAsStateWithLifecycle()
                        val recipes by sharedSearchRecipeViewModel.filteredRecipes.collectAsStateWithLifecycle()

                        SearchingScreen(
                            filters = filters,
                            resultCount = recipes.size,
                            onClose = { navController.popBackStack() },
                            onShowResultsClick = { navController.navigate(Search.Searched()) },
                            onResetFilters = sharedSearchRecipeViewModel::resetFilters,
                            onToggleDifficulty = sharedSearchRecipeViewModel::toggleDifficulty,
                            onToggleCost = sharedSearchRecipeViewModel::toggleCost,
                            onNameQueryChange = sharedSearchRecipeViewModel::updateNameQuery,
                            onIngredientQueryChange = sharedSearchRecipeViewModel::updateIngredientQuery
                        )
                    }
                    composable<Search.Searched> { backStackEntry ->
                        val searched: Search.Searched = backStackEntry.toRoute()
                        com.example.gustoria.ui.recipe.RecipeScreen(
                            navCtrl = navController,
                            recipeRepository = recipeRepository,
                            userRepository = userRepository,
                            initialRecipeId = searched.recipeId,
                            viewModel = sharedSearchRecipeViewModel
                        )
                    }
                }

                composable<Create> {
                    EditRecipeScreen(
                        navController = navController,
                        recipeRepository = recipeRepository,
                        recipeId = null
                    )
                }

                composable<Edit> { backStackEntry ->
                    val edit: Edit = backStackEntry.toRoute()
                    EditRecipeScreen(
                        navController = navController,
                        recipeRepository = recipeRepository,
                        recipeId = edit.recipeId
                    )
                }

                composable<RecipeDetails> { backStackEntry ->
                    val args: RecipeDetails = backStackEntry.toRoute()
                    RecipeDetailsScreen(
                        recipeId = args.recipeId,
                        navCtrl = navController,
                        recipeRepository = recipeRepository,
                        reviewRepository = reviewRepository,
                        userRepository = userRepository
                    )
                }

                composable<AddReview> { backStackEntry ->
                    val args: AddReview = backStackEntry.toRoute()
                    com.example.gustoria.ui.review.ReviewFormScreen(
                        recipeId = args.recipeId,
                        navController = navController,
                        reviewRepository = reviewRepository
                    )
                }

                composable<ReviewsList> { backStackEntry ->
                    val args: ReviewsList = backStackEntry.toRoute()
                    com.example.gustoria.ui.review.ReviewsListScreen(
                        recipeId = args.recipeId,
                        navController = navController,
                        reviewRepository = reviewRepository,
                        userRepository = userRepository
                    )
                }

                composable<OtherProfile> { backStackEntry ->
                    val args: OtherProfile = backStackEntry.toRoute()
                    OtherProfileScreen(
                        userRepo = userRepository,
                        viewedUserId = args.userId,
                        onBack = { navController.popBackStack() }
                    )
                }

                navigation<Favourite>(startDestination = Favourite.Saved) {
                    composable<Favourite.Saved> {
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            recipeRepository = recipeRepository,
                            userRepository = userRepository,
                            initialTab = 0,
                            navController = navController,
                            vm = sharedCollectionViewModel
                        )
                    }
                    composable<Favourite.Tried> {
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            recipeRepository = recipeRepository,
                            userRepository = userRepository,
                            initialTab = 1,
                            navController = navController,
                            vm = sharedCollectionViewModel
                        )
                    }
                    composable<Favourite.Created> {
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            recipeRepository = recipeRepository,
                            userRepository = userRepository,
                            initialTab = 2,
                            navController = navController,
                            vm = sharedCollectionViewModel
                        )
                    }
                    composable<Favourite.Filtering> {
                        val filters by sharedCollectionViewModel.filters.collectAsStateWithLifecycle()
                        val recipes by sharedCollectionViewModel.recipesToShow.collectAsStateWithLifecycle()

                        SearchingScreen(
                            filters = filters,
                            resultCount = recipes.size,
                            onClose = { navController.popBackStack() },
                            onShowResultsClick = { navController.popBackStack() }, // Goes back to list
                            onResetFilters = sharedCollectionViewModel::resetFilters,
                            onToggleDifficulty = sharedCollectionViewModel::toggleDifficulty,
                            onToggleCost = sharedCollectionViewModel::toggleCost,
                            onNameQueryChange = sharedCollectionViewModel::setNameQuery,
                            onIngredientQueryChange = sharedCollectionViewModel::setIngredientQuery
                        )
                    }
                }

                navigation<Profile>(startDestination = Profile.OverallProfile) {
                    composable<Profile.OverallProfile> {
                        OwnedProfileScreen(
                            userRepo = userRepository,
                            navController = navController
                        )
                    }
                    navigation<Profile.ProfileInfo>(startDestination = Profile.ProfileInfo.OverallProfileInfo) {
                        composable<Profile.ProfileInfo.OverallProfileInfo> {
                            Column(
                            ) {
                                Text("Profile Info Overview")
                                Button(onClick = { navController.navigate(Profile.ProfileInfo.CulinaryPreference) }) {Text("Set Culinary Preference") }
                                Button(onClick = { navController.navigate(Profile.ProfileInfo.DietPreference) }) {Text("Set Diet Preference") }
                            }
                        }
                        dialog<Profile.ProfileInfo.CulinaryPreference> {
                            Text("Culinary Preference")
                        }
                        dialog<Profile.ProfileInfo.DietPreference> {
                            Text("Diet Preference")
                        }
                    }
                    composable<Profile.Settings> {
                        Text("Settings")
                    }
                    composable<Profile.HelpAndFeedback> {
                        Text("Help & Feedback")
                    }
                    dialog<Profile.SignOut> {
                        Text("Sign Out")
                    }
                }
            }
        }
    )
}