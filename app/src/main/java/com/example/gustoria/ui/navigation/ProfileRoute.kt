package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringArrayResource
import com.example.gustoria.R
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.ui.user.HelpAndFeedbackDialogue
import com.example.gustoria.ui.user.OwnedProfileScreen
import com.example.gustoria.ui.user.ProfileInfoScreen
import com.example.gustoria.ui.user.SettingsScreen
import com.example.gustoria.ui.user.SignOutDialogue
import com.example.gustoria.ui.user.components.SelectionDialogContent
import com.example.gustoria.viewmodel.OwnedProfileViewModel
import com.example.gustoria.viewmodel.SettingsViewModel
import com.example.gustoria.ui.user.OtherProfileScreen
import com.example.gustoria.viewmodel.OtherProfileViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


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
        @Serializable
        object MealPreference    }
    @Serializable
    object Settings
    @Serializable
    object HelpAndFeedback
    @Serializable
    object SignOut
}

@Composable
fun OtherProfileDestination(
    userId: String,
    navActions: GustoriaNavigationActions,
    viewModel: OtherProfileViewModel = viewModel(
        factory = OtherProfileViewModel.factory(userId)
    )
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val recipeCount by viewModel.recipeCount.collectAsStateWithLifecycle()
    val likeCount by viewModel.likeCount.collectAsStateWithLifecycle()
    val isFollowing by viewModel.isFollowing.collectAsStateWithLifecycle()
    val recipes by viewModel.recipes.collectAsStateWithLifecycle()

    OtherProfileScreen(
        user = user,
        recipeCount = recipeCount,
        likeCount = likeCount,
        isFollowing = isFollowing,
        recipes = recipes,
        onBack = navActions::navigateBack,
        onToggleFollow = {
            if (SessionManagerFacade.isLoggedIn) {
                viewModel.toggleFollow()
            } else {
                navActions.navigateToActionRequirement("follow this user")
            }
        },
        onRecipeClick = navActions::navigateToRecipeDetails
    )
}

@Composable
fun OverallProfileDestination(
    navActions: GustoriaNavigationActions,
    showBackButton: Boolean = true,
    viewModel: OwnedProfileViewModel = viewModel(factory = OwnedProfileViewModel.Factory)
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val recipeCount by viewModel.recipeCount.collectAsStateWithLifecycle()
    val likeCount by viewModel.likeCount.collectAsStateWithLifecycle()

    OwnedProfileScreen(
        user = user,
        recipeCount = recipeCount,
        likeCount = likeCount,
        isLoggedIn = SessionManagerFacade.isLoggedIn,
        showBackButton = showBackButton,
        onBack = navActions::navigateBack,
        onNavigateToProfileInfo = {
            if (SessionManagerFacade.isLoggedIn) {
                navActions.navigateToOverallProfileInfo()
            } else {
                navActions.navigateToActionRequirement("view and edit your detailed profile information")
            }
        },
        onNavigateToSettings = navActions::navigateToSettings,
        onNavigateToHelp = navActions::navigateToHelpAndFeedback,
        onSignOut = navActions::navigateToSignOut,
        onSignIn = navActions::navigateToAuthentication
    )
}

@Composable
fun ProfileInfoDestination(
    navActions: GustoriaNavigationActions,
    viewModel: OwnedProfileViewModel = viewModel(factory = OwnedProfileViewModel.Factory)
) {
    val user by viewModel.user.collectAsStateWithLifecycle()

    ProfileInfoScreen(
        user = user,
        viewModel = viewModel,
        onBack = {
            viewModel.cancelEditing()
            navActions.navigateBack()
        },
        onSave = navActions::navigateBack,
        onAddCuisine = navActions::navigateToCulinaryPreference,
        onAddDiet = navActions::navigateToDietPreference,
        onAddMeal = navActions::navigateToMealPreference
    )
}

@Composable
fun SettingsDestination(
    navActions: GustoriaNavigationActions
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: SettingsViewModel = viewModel(context as androidx.activity.ComponentActivity)

    SettingsScreen(
        viewModel = viewModel,
        onBack = navActions::navigateBack
    )
}

@Composable
fun CulinaryPreferenceDestination(
    navActions: GustoriaNavigationActions,
    viewModel: OwnedProfileViewModel = viewModel(factory = OwnedProfileViewModel.Factory)
) {
    val options = stringArrayResource(R.array.recipe_cuisine_types).toList()
    SelectionDialogContent(
        title = "Select Cuisine",
        options = options,
        selectedOptions = viewModel.editableUser?.cuisinePreferences ?: emptyList(),
        onToggleOption = viewModel::toggleCuisinePreference,
        onDismiss = navActions::navigateBack
    )
}

@Composable
fun DietPreferenceDestination(
    navActions: GustoriaNavigationActions,
    viewModel: OwnedProfileViewModel = viewModel(factory = OwnedProfileViewModel.Factory)
) {
    val options = stringArrayResource(R.array.recipe_dietary_tags).toList()
    SelectionDialogContent(
        title = "Select Diet Preference",
        options = options,
        selectedOptions = viewModel.editableUser?.dietaryRestrictions ?: emptyList(),
        onToggleOption = viewModel::toggleDietaryRestriction,
        onDismiss = navActions::navigateBack
    )
}
@Composable
fun MealPreferenceDestination(
    navActions: GustoriaNavigationActions,
    viewModel: OwnedProfileViewModel = viewModel(factory = OwnedProfileViewModel.Factory)
) {
    val options = stringArrayResource(R.array.recipe_meal_types).toList()
    SelectionDialogContent(
        title = "Select Favourite Meal",
        options = options,
        selectedOptions = viewModel.editableUser?.favouriteMealTypes ?: emptyList(),
        onToggleOption = viewModel::toggleMealType,
        onDismiss = navActions::navigateBack
    )
}

@Composable
fun HelpAndFeedbackDestination(
    navActions: GustoriaNavigationActions
) {
    HelpAndFeedbackDialogue(
        onDismiss = navActions::navigateBack
    )
}

@Composable
fun SignOutDestination(
    navActions: GustoriaNavigationActions
) {
    val scope = rememberCoroutineScope()
    SignOutDialogue(
        onSignOut = {
            scope.launch {
                SessionManagerFacade.logOut()
                navActions.navigateToHomeOnSignOut()
            }
        },
        onDismiss = navActions::navigateBack
    )
}
