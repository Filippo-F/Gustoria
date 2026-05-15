package com.example.gustoria.ui.authentication

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.ui.HomeScreen
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview

@MultiPreview
@Preview
@Composable
fun HomeScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        AuthenticationDialogue()
    }
}

@Composable
fun AuthenticationDialogue () {

}