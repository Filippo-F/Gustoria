package com.example.gustoria.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.ui.navigateUp
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.ui.recipe.RecipeScreen
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.ui.utils.PreviewUtils
import kotlinx.serialization.Serializable

@MultiPreview
@Preview
@Composable
fun HomeScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        HomeScreen()
    }
}

@Serializable
object HomeRoute

@Serializable
object SearchRoute

@Serializable
object CreateRoute

@Serializable
object FavouriteRoute

@Serializable
object ProfileRoute

class Actions(val navCtrl : NavHostController) {
    val goHome: () -> Unit = {
        navCtrl.navigate(HomeRoute)
    }

    val navigateBack: () -> Unit = {
        navCtrl.popBackStack()
    }
}

@Composable
fun HomeScreen(
) {
    val navController = rememberNavController()
    val actions = remember(navController) {
        Actions(navController)
    }
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
                startDestination = HomeRoute,
                modifier = Modifier.padding(paddingValues)
            ){
                composable<HomeRoute> {
                    Text("Home Route")
                }

                composable<SearchRoute> {
                    Text("Search Route")
                }

                composable<CreateRoute> {
                    Text("Create Route")
                }

                composable<FavouriteRoute> {
                    Text("Favourite Route")
                }

                composable<ProfileRoute> {
                    Text("Profile Route")
                }
            }
        }
    )
}
