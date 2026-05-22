package com.example.gustoria.ui.navigation

import androidx.navigation.NavController

class GustoriaNavigationActions(private val navController: NavController) {
    fun navigateBack() {
        navController.popBackStack()
    }
}