package com.example.gustoria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.Authentication
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview

@MultiPreview
@Preview
@Composable
fun HomeScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        HomeScreen(navCtrl = rememberNavController())
    }
}

class HomeActions (
    val navCtrl: NavHostController
) {
    val navigateAuth: () -> Unit = {
        navCtrl.navigate(Authentication)
    }
}

@Composable
fun HomeScreen(
    navCtrl: NavHostController,
) {
    val actions = remember(navCtrl) {
        HomeActions(navCtrl)
    }
    Column(

    ) {
        Text("Home Screen")
        Button(onClick = actions.navigateAuth) {Text("Login") }
    }
}
