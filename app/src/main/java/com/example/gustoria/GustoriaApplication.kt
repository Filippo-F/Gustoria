package com.example.gustoria

import android.app.Application
import com.example.gustoria.data.AppContainer
import com.example.gustoria.data.DefaultAppContainer
import com.example.gustoria.data.firebaseRepo.FirebaseRecipeRepo
import com.example.gustoria.data.firebaseRepo.FirebaseReviewRepo
import com.example.gustoria.data.firebaseRepo.FirebaseUserRepo
import com.example.gustoria.data.firebaseRepo.FirebaseNotificationRepo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GustoriaApplication : Application() {

    companion object {
        lateinit var instance: GustoriaApplication
            private set
    }

    // AppContainer instance used by the rest of classes to obtain dependencies
    
    lateinit var container: AppContainer
    lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = DefaultAppContainer(context = applicationContext)
        auth = Firebase.auth
        // Initialize Firebase data if necessary
        MainScope().launch(Dispatchers.IO) {
            (container.userRepository as? FirebaseUserRepo)?.initializeData()
            (container.recipeRepository as? FirebaseRecipeRepo)?.initializeData()
            (container.reviewRepository as? FirebaseReviewRepo)?.initializeData()
            (container.notificationRepository as? FirebaseNotificationRepo)?.initializeData()
        }
    }
}