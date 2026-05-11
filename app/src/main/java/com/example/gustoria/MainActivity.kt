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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.ui.HomeScreen
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
fun GustoriaApp(recipeRepository: RecipeRepoInterface) {
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
                    HomeScreen(navCtrl = navController)
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