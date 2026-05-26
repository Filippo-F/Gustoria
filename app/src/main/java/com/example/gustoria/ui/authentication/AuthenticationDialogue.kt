package com.example.gustoria.ui.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
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

@Composable
fun AuthenticationDialogue(
    onAuthSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .widthIn(max = 450.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Gustoria",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose a way to sign in",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.signInWithGoogle(context, onAuthSuccess) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign in with Google")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { viewModel.signInAnonymous(context, onAuthSuccess) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign in Anonymously")
                }
            }

            state.errors["auth"]?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
