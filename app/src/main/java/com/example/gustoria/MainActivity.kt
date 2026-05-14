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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.gustoria.ui.HomeScreen
import com.example.gustoria.ui.recipe.EditRecipeScreen
import com.example.gustoria.ui.recipe.RecipeScreen
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private lateinit var recipeRepository: RecipeRepoInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Paper.init(applicationContext)
        recipeRepository = PaperRecipeRepo()

        setContent {
            GustoriaTheme {
                GustoriaApp(recipeRepository = recipeRepository)
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
    object Searched
}

@Serializable
object Create

@Serializable
data class Edit(val recipeId: String)



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
object Authentication {
    @Serializable
    object Login
    @Serializable
    object Register
}

@Serializable
object Favourite {
    @Serializable
    object Saved
    @Serializable
    object Tried
    @Serializable
    object Created
}

@Composable
fun GustoriaApp(recipeRepository: RecipeRepoInterface) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

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
                navigation<Authentication>(startDestination = Authentication.Login) {
                    dialog<Authentication.Login> {
                        Text("Login")
                    }
                    dialog<Authentication.Register> {
                        Text("Register")
                    }
                }
                composable<Home> {
                    HomeScreen(navCtrl = navController)
                }

                navigation<Search>(startDestination = Search.FeaturedSearch) {
                    composable<Search.FeaturedSearch> {
                        Column (

                        ) {
                            Text("Featured Search")
                            Button(onClick = {navController.navigate(Search.Searching)}){Text("Search")}
                        }
                    }
                    composable<Search.Searching> {
                        Column (

                        ) {
                            Text("Searching...")
                            Button(onClick = {navController.navigate(Search.Searched)}){Text("Confirm")}
                        }
                    }
                    composable<Search.Searched> {
                        RecipeScreen(
                            navCtrl = navController,
                            recipeRepository = recipeRepository
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

                navigation<Favourite>(startDestination = Favourite.Saved) {
                    composable<Favourite.Saved> {
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            recipeRepository = recipeRepository,
                            initialTab = 0,
                            navController = navController
                        )
                    }
                    composable<Favourite.Tried> {
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            recipeRepository = recipeRepository,
                            initialTab = 1,
                            navController = navController
                        )
                    }
                    composable<Favourite.Created> {
                        com.example.gustoria.ui.recipe.RecipeCollectionScreen(
                            recipeRepository = recipeRepository,
                            initialTab = 2,
                            navController = navController
                        )
                    }
                }

                navigation<Profile>(startDestination = Profile.OverallProfile) {
                    composable<Profile.OverallProfile> {
                        OwnedProfileScreen(
                            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
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