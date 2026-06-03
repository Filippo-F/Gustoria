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
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val authState: AuthState = AuthState.Unauthenticated,
    val nickname: String = "",
    val name: String = "",
    val surname: String = "",
    val phoneNumber: String = "",
    val showRegistration: Boolean = false
)

class AuthenticationViewModel(
    private val userRepo: UserRepoInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    
    val state: StateFlow<AuthUiState> = combine(
        _uiState,
        SessionManagerFacade.authState
    ) { uiState, authState ->
        uiState.copy(
            authState = authState,
            isLoading = authState is AuthState.Registering
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthUiState()
    )

    fun updateNickname(value: String) {
        _uiState.update { it.copy(nickname = value, errors = it.errors - "nickname") }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, errors = it.errors - "name") }
    }

    fun updateSurname(value: String) {
        _uiState.update { it.copy(surname = value, errors = it.errors - "surname") }
    }

    fun updatePhoneNumber(value: String) {
        _uiState.update { it.copy(phoneNumber = value, errors = it.errors - "phoneNumber") }
    }

    fun signInWithGoogle(context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(errors = emptyMap()) }
            val result = SessionManagerFacade.signIn(context)
            result.onSuccess {
                val userId = SessionManagerFacade.currentUserId.value
                if (userId != null) {
                    val user = userRepo.getUserById(userId).first()
                    if (user == null) {
                        _uiState.update { it.copy(showRegistration = true) }
                    } else {
                        onSuccess(userId)
                    }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(errors = mapOf("auth" to (e.message ?: "Authentication failed"))) }
            }
        }
    }

    fun signInAnonymous(context: Context, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(errors = emptyMap()) }
            val result = SessionManagerFacade.signInAnonymous(context)
            result.onSuccess {
                val userId = SessionManagerFacade.currentUserId.value
                if (userId != null) {
                    val user = userRepo.getUserById(userId).first()
                    if (user == null) {
                        _uiState.update { it.copy(showRegistration = true) }
                    } else {
                        onSuccess(userId)
                    }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(errors = mapOf("auth" to (e.message ?: "Authentication failed"))) }
            }
        }
    }

    private fun validateFields(): Boolean {
        val current = _uiState.value
        val newErrors = mutableMapOf<String, String>()
        if (current.nickname.isBlank()) newErrors["nickname"] = "Nickname is required"
        if (current.name.isBlank()) newErrors["name"] = "Name is required"
        if (current.surname.isBlank()) newErrors["surname"] = "Surname is required"
        if (current.phoneNumber.isBlank()) newErrors["phoneNumber"] = "Phone number is required"

        _uiState.update { it.copy(errors = newErrors) }
        return newErrors.isEmpty()
    }

    fun completeRegistration(
        onSuccess: (String) -> Unit
    ) {
        if (!validateFields()) return

        viewModelScope.launch {
            val userId = SessionManagerFacade.currentUserId.value
            val current = _uiState.value
            if (userId != null) {
                try {
                    val newUser = User(
                        internalId = userId,
                        nickname = current.nickname,
                        firstName = current.name,
                        lastName = current.surname,
                        phoneNumber = current.phoneNumber
                    )
                    userRepo.createUser(newUser)
                    _uiState.update { it.copy(showRegistration = false) }
                    onSuccess(userId)
                } catch (e: Exception) {
                    _uiState.update { it.copy(errors = mapOf("registration" to (e.message ?: "Registration failed"))) }
                }
            } else {
                _uiState.update { it.copy(errors = mapOf("registration" to "User not found")) }
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
