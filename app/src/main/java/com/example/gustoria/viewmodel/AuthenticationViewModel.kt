package com.example.gustoria.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.data.auth.AuthState
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val authState: AuthState = AuthState.Unauthenticated
)

class AuthenticationViewModel(
    private val userRepo: UserRepoInterface
) : ViewModel() {

    private val _errors = MutableStateFlow<Map<String, String>>(emptyMap())
    
    val state: StateFlow<AuthUiState> = combine(
        _errors,
        SessionManagerFacade.authState
    ) { errors, authState ->
        AuthUiState(
            errors = errors,
            isLoading = authState is AuthState.Registering,
            authState = authState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthUiState()
    )

    fun signInWithGoogle(context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _errors.update { emptyMap() }
            val result = SessionManagerFacade.signIn(context)
            result.onSuccess {
                SessionManagerFacade.currentUserId?.let { onSuccess(it) }
            }.onFailure { e ->
                _errors.update { mapOf("auth" to (e.message ?: "Authentication failed")) }
            }
        }
    }

    fun signInAnonymous(context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _errors.update { emptyMap() }
            val result = SessionManagerFacade.signInAnonymous(context)
            result.onSuccess {
                SessionManagerFacade.currentUserId?.let { onSuccess(it) }
            }.onFailure { e ->
                _errors.update { mapOf("auth" to (e.message ?: "Authentication failed")) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val userRepository = application.container.userRepository
                AuthenticationViewModel(userRepository)
            }
        }
    }
}
