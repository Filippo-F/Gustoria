package com.example.gustoria.ui.authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.viewmodel.AuthUiState
import com.example.gustoria.viewmodel.AuthenticationViewModel

@MultiPreview
@Preview
@Composable
fun AuthScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        AuthenticationDialogue(
            onAuthSuccess = {},
            viewModel = viewModel(factory = AuthenticationViewModel.Factory)
        )
    }
}

@MultiPreview
@Preview
@Composable
fun ActionRequirementPreview() {
    GustoriaTheme(dynamicColor = false) {
        ActionRequirementDialogue(
            action = "create a recipe",
            onAuthSuccess = {},
            viewModel = viewModel(factory = AuthenticationViewModel.Factory)
        )
    }
}

@MultiPreview
@Preview
@Composable
fun RegistrationPreview() {
    GustoriaTheme(dynamicColor = false) {
        UserRegistrationDialogue(
            state = AuthUiState(
                nickname = "ChefMario",
                name = "Mario",
                surname = "Rossi",
                phoneNumber = "123456789"
            ),
            onNicknameChange = {},
            onNameChange = {},
            onSurnameChange = {},
            onPhoneNumberChange = {},
            onRegister = {}
        )
    }
}

@Composable
fun RegistrationDialogue(
    onRegistrationSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UserRegistrationDialogue(
        state = state,
        onNicknameChange = viewModel::updateNickname,
        onNameChange = viewModel::updateName,
        onSurnameChange = viewModel::updateSurname,
        onPhoneNumberChange = viewModel::updatePhoneNumber,
        onRegister = {
            viewModel.completeRegistration(
                onSuccess = onRegistrationSuccess
            )
        }
    )
}

@Composable
fun AuthenticationDialogue(
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AuthBaseDialogue(
        title = "Welcome to Gustoria",
        subtitle = "Choose a way to sign in",
        state = state,
        onSignInGoogle = { viewModel.signInWithGoogle(context, onAuthSuccess) },
        onSignInAnonymous = { viewModel.signInAnonymous(context, onAuthSuccess) }
    )
}

@Composable
fun ActionRequirementDialogue(
    action: String,
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AuthBaseDialogue(
        title = "Authentication Required",
        subtitle = "To $action you must be logged in",
        state = state,
        onSignInGoogle = { viewModel.signInWithGoogle(context, onAuthSuccess) },
        onSignInAnonymous = { viewModel.signInAnonymous(context, onAuthSuccess) },
        onSignInAnonymousLabel = "Continue as Guest"
    )
}
