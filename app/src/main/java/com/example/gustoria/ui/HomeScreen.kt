package com.example.gustoria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.Authentication.Login
import com.example.gustoria.Authentication.Register
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
    val navigateLogin: () -> Unit = {
        navCtrl.navigate(Login)
    }
    val navigateRegister: () -> Unit = {
        navCtrl.navigate(Register)
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
        Button(onClick = actions.navigateLogin) {Text("Login") }
        Button(onClick = actions.navigateRegister) {Text("Register") }
    }
}
