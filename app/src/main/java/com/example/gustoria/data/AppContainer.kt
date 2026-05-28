package com.example.gustoria.data

import android.content.Context
import androidx.credentials.CredentialManager
import com.example.gustoria.data.auth.FirebaseAuthRepository
import com.example.gustoria.domain.LikeRepoInterface
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import com.example.gustoria.data.paperRepo.PaperLikeRepo
import com.example.gustoria.data.paperRepo.PaperRecipeRepo
import com.example.gustoria.data.paperRepo.PaperReviewRepo
import com.example.gustoria.data.paperRepo.PaperUserRepo
import com.example.gustoria.domain.AuthRepoInterface
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.gustoria.data.paperRepo.PaperNotificationRepo
import com.example.gustoria.domain.NotificationRepoInterface

interface AppContainer {
    val recipeRepository: RecipeRepoInterface
    val userRepository: UserRepoInterface
    val reviewRepository: ReviewRepoInterface
    val likeRepository: LikeRepoInterface
    val notificationRepository: NotificationRepoInterface
    val authRepository: AuthRepoInterface
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    override val recipeRepository: RecipeRepoInterface by lazy {
        PaperRecipeRepo()
    }

    override val userRepository: UserRepoInterface by lazy {
        PaperUserRepo()
    }

    override val reviewRepository: ReviewRepoInterface by lazy {
        PaperReviewRepo()
    }

    override val likeRepository: LikeRepoInterface by lazy {
        PaperLikeRepo()
    }

    override val notificationRepository: NotificationRepoInterface by lazy {
        PaperNotificationRepo()
    }

    override val authRepository: AuthRepoInterface by lazy {
        FirebaseAuthRepository(Firebase.auth, credentialManager)
    }
}
