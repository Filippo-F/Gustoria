package com.example.gustoria.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.gustoria.R
import com.example.gustoria.domain.AuthRepoInterface
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) : AuthRepoInterface {
    private val _currentUser = MutableStateFlow(auth.currentUser?.uid)

    init {
        auth.addAuthStateListener { _currentUser.value = it.currentUser?.uid }
    }

    override suspend fun signIn(context: Context): Result<Unit> {
        return try {
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                serverClientId = context.getString(R.string.default_web_client_id)
            ).build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            auth.signInWithCredential(firebaseCredential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override val isLoggedIn: Boolean
        get() = _currentUser.value != null

    override suspend fun logOut() {
        auth.signOut()
    }

    override val authState: StateFlow<AuthState>
        get() = TODO("Not yet implemented")

    private val _currentUserId = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid)
    override val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()
    override val currentUserState: StateFlow<String?> = _currentUser.asStateFlow()
}