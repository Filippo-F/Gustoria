package com.example.gustoria.domain

import android.content.Context
import com.example.gustoria.data.auth.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepoInterface {
    val currentUserId: String?
    val currentUserState: Flow<String?>
    val currentUserStateFlow: StateFlow<String?>
    suspend fun signIn(context: Context): Result<Boolean>
    suspend fun signInAnonymous(context: Context): Result<Boolean>
    val isLoggedIn: Boolean
    suspend fun logOut()
    val authState: StateFlow<AuthState>
}