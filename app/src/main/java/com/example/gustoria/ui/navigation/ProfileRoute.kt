package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.user.HelpAndFeedbackDialogue
import com.example.gustoria.ui.user.OwnedProfileScreen
import com.example.gustoria.ui.user.ProfileInfoScreen
import com.example.gustoria.ui.user.SettingsScreen
import com.example.gustoria.ui.user.SignOutDialogue
import com.example.gustoria.viewmodel.OwnedProfileViewModel
import com.example.gustoria.viewmodel.SettingsViewModel
import kotlinx.serialization.Serializable

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

@Composable
fun OverallProfileDestination(
    navActions: GustoriaNavigationActions,
    viewModel: OwnedProfileViewModel = viewModel(factory = OwnedProfileViewModel.Factory)
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val recipeCount by viewModel.recipeCount.collectAsStateWithLifecycle()

    OwnedProfileScreen(
        user = user,
        recipeCount = recipeCount,
        onBack = navActions::navigateBack,
        onNavigateToProfileInfo = navActions::navigateToOverallProfileInfo,
        onNavigateToSettings = navActions::navigateToSettings,
        onNavigateToHelp = navActions::navigateToHelpAndFeedback,
        onSignOut = navActions::navigateToSignOut
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
        onSave = navActions::navigateBack
    )
}

@Composable
fun SettingsDestination(
    navActions: GustoriaNavigationActions,
    viewModel: SettingsViewModel = viewModel()
) {
    SettingsScreen(
        viewModel = viewModel,
        onBack = navActions::navigateBack
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
    SignOutDialogue(
        onSignOut = {
            navActions.navigateBack()
        },
        onDismiss = navActions::navigateBack
    )
}
