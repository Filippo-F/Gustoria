package com.example.gustoria.ui.authentication

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.viewmodel.AuthenticationViewModel

@MultiPreview
@Preview
@Composable
fun AuthScreenPreview() {
    val fakeRepo = com.example.gustoria.ui.utils.PreviewUtils.createFakeUserRepo()
    GustoriaTheme(dynamicColor = false) {
        AuthenticationDialogue(userRepo = fakeRepo, onAuthSuccess = {})
    }
}

@Composable
fun AuthenticationDialogue(
    userRepo: UserRepoInterface,
    onAuthSuccess: (String) -> Unit,
    vm: AuthenticationViewModel = viewModel(
        factory = AuthenticationViewModel.factory(userRepo)
    )
) {

}

@Composable
fun LoginView() {

}

@Composable
fun RegisterView() {

}