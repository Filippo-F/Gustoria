package com.example.gustoria.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.data.auth.SessionManagerFacade
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
)

class AuthenticationViewModel(
    private val userRepo: UserRepoInterface
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun signInWithGoogle(context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errors = emptyMap()) }
            val result = SessionManagerFacade.signIn(context)
            _state.update { it.copy(isLoading = false) }
            result.onSuccess {
                try {
                    SessionManagerFacade.currentUserId?.let { onSuccess(it) }
                } catch (e: IllegalStateException) {
                    _state.update { it.copy(errors = mapOf("auth" to (e.message ?: "User not found after sign in"))) }
                }
            }.onFailure { e ->
                _state.update { it.copy(errors = mapOf("auth" to (e.message ?: "Authentication failed"))) }
            }
        }
    }

    fun signInAnonymous(context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errors = emptyMap()) }
            val result = SessionManagerFacade.signInAnonymous(context)
            _state.update { it.copy(isLoading = false) }
            result.onSuccess {
                try {
                    SessionManagerFacade.currentUserId?.let { onSuccess(it) }
                } catch (e: IllegalStateException) {
                    _state.update { it.copy(errors = mapOf("auth" to (e.message ?: "User not found after sign in"))) }
                }
            }.onFailure { e ->
                _state.update { it.copy(errors = mapOf("auth" to (e.message ?: "Authentication failed"))) }
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
