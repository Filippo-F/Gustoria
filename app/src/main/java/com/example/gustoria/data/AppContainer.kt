package com.example.gustoria.data

import android.content.Context
import androidx.credentials.CredentialManager
import com.example.gustoria.data.auth.FirebaseAuthRepository
import com.example.gustoria.data.firebaseRepo.FirebaseUserRepo
import com.example.gustoria.data.firebaseRepo.FirebaseRecipeRepo
import com.example.gustoria.data.firebaseRepo.FirebaseReviewRepo
import com.example.gustoria.domain.LikeRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.data.paperRepo.PaperLikeRepo
import com.example.gustoria.domain.AuthRepoInterface
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

interface AppContainer {
    val recipeRepository: RecipeRepoInterface
    val userRepository: UserRepoInterface
    val reviewRepository: ReviewRepoInterface
    val likeRepository: LikeRepoInterface
    val authRepository: AuthRepoInterface
}

// [AppContainer] implementation that provides Firestore-backed repositories

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    override val recipeRepository: RecipeRepoInterface by lazy {
        FirebaseRecipeRepo(firestore)
    }

    override val userRepository: UserRepoInterface by lazy {
        FirebaseUserRepo(firestore)
    }

    override val reviewRepository: ReviewRepoInterface by lazy {
        FirebaseReviewRepo(firestore)
    }

    override val likeRepository: LikeRepoInterface by lazy {
        PaperLikeRepo()
    }

    override val authRepository: AuthRepoInterface by lazy {
        FirebaseAuthRepository(Firebase.auth, credentialManager)
    }
}
