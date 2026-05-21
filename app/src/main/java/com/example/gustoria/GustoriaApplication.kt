package com.example.gustoria

import android.app.Application
import com.example.gustoria.model.AppContainer
import com.example.gustoria.model.DefaultAppContainer
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GustoriaApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        Paper.init(applicationContext)
        container = DefaultAppContainer(context = applicationContext)
        auth = Firebase.auth
        // Initialize Firebase data if necessary
        MainScope().launch(Dispatchers.IO) {
        }
    }
}