package com.example.gustoria.data.auth

import android.content.Context
import com.example.gustoria.R
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.gustoria.domain.AuthRepoInterface
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Define UI Auth States
sealed interface AuthState {
    object Registering : AuthState
    object Authenticated : AuthState
    object Unauthenticated : AuthState
}

object SessionManagerFacade : AuthRepoInterface {
    private val _currentUserId = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid)
    override val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    override val currentUserState: Flow<String?> = callbackFlow {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private val _authState = MutableStateFlow<AuthState>(
        FirebaseAuth.getInstance().currentUser?.let {
            if (it.isAnonymous) AuthState.Unauthenticated else AuthState.Authenticated
        } ?: AuthState.Unauthenticated
    )
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            _currentUserId.value = user?.uid
            _authState.value = when {
                user == null || user.isAnonymous -> AuthState.Unauthenticated
                else -> AuthState.Authenticated
            }
        }
    }

    override suspend fun signIn(context: Context): Result<Unit> {
        return try {
            _authState.value = AuthState.Registering
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                serverClientId = context.getString(R.string.default_web_client_id)
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = CredentialManager.create(context).getCredential(context, request)
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            _authState.value = AuthState.Unauthenticated
            Result.failure(e)
        }
    }

    override suspend fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    override val isLoggedIn: Boolean
        get() = FirebaseAuth.getInstance().currentUser?.isAnonymous == false
}
