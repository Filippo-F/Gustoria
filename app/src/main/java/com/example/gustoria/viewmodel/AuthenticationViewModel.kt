package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN, REGISTER
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nickname: String = "",
    val name: String = "",
    val surname: String = "",
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
)

class AuthenticationViewModel(
    private val userRepo: UserRepoInterface
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errors = it.errors - "email") }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errors = it.errors - "password") }
    }

    fun onConfirmPasswordChange(password: String) {
        _state.update { it.copy(confirmPassword = password, errors = it.errors - "confirmPassword") }
    }

    fun onNicknameChange(nickname: String) {
        _state.update { it.copy(nickname = nickname, errors = it.errors - "nickname") }
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, errors = it.errors - "name") }
    }

    fun onSurnameChange(surname: String) {
        _state.update { it.copy(surname = surname, errors = it.errors - "surname") }
    }

    fun switchMode(mode: AuthMode) {
        _state.update { 
            it.copy(
                mode = mode,
                password = "",
                confirmPassword = "",
                errors = emptyMap()
            ) 
        }
    }

    fun authenticate(onSuccess: (String) -> Unit) {
        val currentState = _state.value
        if (currentState.mode == AuthMode.LOGIN) {
            login(onSuccess)
        } else {
            register(onSuccess)
        }
    }

    private fun login(onSuccess: (String) -> Unit) {
        val currentState = _state.value
        val errors = mutableMapOf<String, String>()

        if (currentState.email.isBlank()) errors["email"] = "Email is required"
        if (currentState.password.isBlank()) errors["password"] = "Password is required"

        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors) }
            return
        }

        viewModelScope.launch {
            // TODO : Handling login
        }
    }

    private fun register(onSuccess: (String) -> Unit) {
        val currentState = _state.value
        val errors = mutableMapOf<String, String>()

        if (currentState.email.isBlank()) {
            errors["email"] = "Email is required"
        } else if (!isValidEmail(currentState.email)) {
            errors["email"] = "Invalid email format"
        }

        if (currentState.password.isBlank()) {
            errors["password"] = "Password is required"
        } else if (currentState.password.length < 6) {
            errors["password"] = "Password must be at least 6 characters"
        }

        if (currentState.confirmPassword != currentState.password) {
            errors["confirmPassword"] = "Passwords do not match"
        }

        if (currentState.nickname.isBlank()) {
            errors["nickname"] = "Nickname is required"
        } else if (currentState.nickname.length < 3) {
            errors["nickname"] = "Nickname must be at least 3 characters"
        }

        if (currentState.name.isBlank()) errors["name"] = "Name is required"
        if (currentState.surname.isBlank()) errors["surname"] = "Surname is required"

        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors) }
            return
        }

        viewModelScope.launch {
            // TODO : Handling sign in
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return email.matches(emailRegex)
    }

    companion object {
        fun factory(userRepo: UserRepoInterface): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AuthenticationViewModel(userRepo) as T
            }
    }
}
