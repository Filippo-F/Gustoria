package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
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
data class ActionRequirement(val action: String)

@Composable
fun AuthenticationDestination(
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.showRegistration) {
        RegistrationDialogue(
            onRegistrationSuccess = onAuthSuccess,
            viewModel = viewModel
        )
    } else {
        AuthenticationDialogue(
            onAuthSuccess = onAuthSuccess,
            viewModel = viewModel
        )
    }
}

@Composable
fun ActionRequirementDestination(
    action: String,
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.showRegistration) {
        RegistrationDialogue(
            onRegistrationSuccess = onAuthSuccess,
            viewModel = viewModel
        )
    } else {
        ActionRequirementDialogue(
            action = action,
            onAuthSuccess = onAuthSuccess,
            viewModel = viewModel
        )
    }
}
