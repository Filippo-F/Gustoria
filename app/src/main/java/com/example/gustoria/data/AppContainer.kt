package com.example.gustoria.data

import android.content.Context
import androidx.credentials.CredentialManager
import com.example.gustoria.data.auth.FirebaseAuthRepository
import com.example.gustoria.data.firebaseRepo.FirebaseUserRepo
import com.example.gustoria.data.firebaseRepo.FirebaseRecipeRepo
import com.example.gustoria.data.firebaseRepo.FirebaseReviewRepo
import com.example.gustoria.data.firebaseRepo.FirebaseNotificationRepo
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.domain.AuthRepoInterface
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.example.gustoria.domain.NotificationRepoInterface

interface AppContainer {
    val recipeRepository: RecipeRepoInterface
    val userRepository: UserRepoInterface
    val reviewRepository: ReviewRepoInterface
    val notificationRepository: NotificationRepoInterface
    val authRepository: AuthRepoInterface
}

// [AppContainer] implementation that provides Firestore-backed repositories

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    override val recipeRepository: RecipeRepoInterface by lazy {
        FirebaseRecipeRepo(Firebase.firestore)
    }

    override val userRepository: UserRepoInterface by lazy {
        FirebaseUserRepo(Firebase.firestore)
    }

    override val reviewRepository: ReviewRepoInterface by lazy {
        FirebaseReviewRepo(Firebase.firestore)
    }

    override val notificationRepository: NotificationRepoInterface by lazy {
        FirebaseNotificationRepo(Firebase.firestore)
    }

    override val authRepository: AuthRepoInterface by lazy {
        FirebaseAuthRepository(Firebase.auth, credentialManager)
    }
}
