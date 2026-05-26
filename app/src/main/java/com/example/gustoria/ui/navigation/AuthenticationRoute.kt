package com.example.gustoria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.authentication.ActionRequirementDialogue
import com.example.gustoria.ui.authentication.AuthenticationDialogue
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
    AuthenticationDialogue(
        onAuthSuccess = onAuthSuccess,
        viewModel = viewModel
    )
}

@Composable
fun ActionRequirementDestination(
    action: String,
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    ActionRequirementDialogue(
        action = action,
        onAuthSuccess = onAuthSuccess,
        viewModel = viewModel
    )
}
