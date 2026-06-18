package com.example.gustoria.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.authentication.ActionRequirementDialogue
import com.example.gustoria.ui.authentication.AuthenticationDialogue
import com.example.gustoria.ui.authentication.RegistrationDialogue
import com.example.gustoria.viewmodel.AuthenticationViewModel
import kotlinx.serialization.Serializable

@Serializable
object Authentication

@Serializable
object Registration

@Serializable
data class ActionRequirement(val action: String)

@Composable
fun AuthenticationDestination(
    onAuthSuccess: (String) -> Unit,
    onNeedsRegistration: () -> Unit,
    viewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.showRegistration) {
        if (state.showRegistration) {
            onNeedsRegistration()
        }
    }

    AuthenticationDialogue(
        onAuthSuccess = onAuthSuccess,
        viewModel = viewModel
    )
}

@Composable
fun ActionRequirementDestination(
    action: String,
    onAuthSuccess: (String) -> Unit,
    onNeedsRegistration: () -> Unit,
    viewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.showRegistration) {
        if (state.showRegistration) {
            onNeedsRegistration()
        }
    }

    ActionRequirementDialogue(
        action = action,
        onAuthSuccess = onAuthSuccess,
        viewModel = viewModel
    )
}

@Composable
fun RegistrationDestination(
    onRegistrationSuccess: (String) -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    BackHandler {
        viewModel.cancelRegistration()
        onCancel()
    }

    RegistrationDialogue(
        onRegistrationSuccess = onRegistrationSuccess,
        viewModel = viewModel
    )
}
