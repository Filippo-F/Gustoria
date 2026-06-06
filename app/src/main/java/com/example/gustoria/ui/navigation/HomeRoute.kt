package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.ui.HomeScreen
import com.example.gustoria.viewmodel.HomeViewModel
import kotlinx.serialization.Serializable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gustoria.viewmodel.NotificationViewModel


@Serializable
object Home

@Composable
fun HomeDestination(
    navActions: GustoriaNavigationActions,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    notificationViewModel: NotificationViewModel = viewModel(factory = NotificationViewModel.Factory)
) {
    val recommendedRecipes by viewModel.recommendedRecipes.collectAsStateWithLifecycle()
    val myRecipes by viewModel.myRecipes.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val unreadCount by notificationViewModel.unreadCount.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarShown by remember { mutableStateOf(false) }

    LaunchedEffect(unreadCount) {
        if (unreadCount > 0 && !snackbarShown) {
            snackbarShown = true
            snackbarHostState.showSnackbar(
                message = "You have $unreadCount unread notification${if (unreadCount > 1) "s" else ""}",
                duration = SnackbarDuration.Short
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(
            recommendedRecipes = recommendedRecipes,
            myRecipes = myRecipes,
            selectedCategory = selectedCategory,
            unreadCount = unreadCount,
            onNavigateToProfile = {
                if (SessionManagerFacade.isLoggedIn) {
                    navActions.navigateToProfile()
                } else {
                    navActions.navigateToAuthentication()
                }
            },
            onNavigateToNotifications = navActions::navigateToNotifications,
            onNavigateToRecipeDetails = navActions::navigateToRecipeDetails,
            onCategorySelected = viewModel::selectCategory
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}