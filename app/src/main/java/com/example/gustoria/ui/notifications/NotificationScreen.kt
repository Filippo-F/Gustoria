package com.example.gustoria.ui.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview

@MultiPreview
@Preview
@Composable
fun NotificationScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        NotificationScreen(
            navController = rememberNavController()
        )
    }
}

class NotificationActions(val navCtrl: NavHostController) {
    val navigateBack: () -> Unit = {
        navCtrl.popBackStack()
    }
}

@Composable
fun NotificationScreen(navController: NavHostController) {
    val actions = remember(navController) { NotificationActions(navController) }

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                title = "Notifications",
                onBack = actions.navigateBack,
                modifier = Modifier.height(56.dp)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No notification found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
