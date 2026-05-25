package com.example.gustoria.data.auth

import android.content.Context
import com.example.gustoria.R
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.gustoria.domain.AuthRepoInterface
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
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

object SessionManager {
    const val CURRENT_LOGGED_IN_USER_ID: String = "101"
}
object SessionManagerFacade : AuthRepoInterface {
    override val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")


    override val currentUserState: Flow<String?> = callbackFlow{
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }

    }
    private val _currentUser = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid)
    override val currentUserStateFlow: StateFlow<String?> = _currentUser.asStateFlow()
    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser?.uid
        }
    }

    override suspend fun signIn(context: Context): Result<Unit> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id)) // Found in generated resources from google-services.json
                //.setServerClientId("REPLACE_ME_STRING")
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = CredentialManager.create(context).getCredential(context, request)
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInAnonymous(context: Context): Result<Unit> {
        return try {
            FirebaseAuth.getInstance().signInAnonymously().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    override val isLoggedIn: Boolean
        get() = FirebaseAuth.getInstance().currentUser != null
}
