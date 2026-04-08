package com.example.gustoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gustoria.ui.theme.GustoriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GustoriaTheme {
                Column(Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Row(Modifier,
                        horizontalArrangement = Arrangement.Center) {
                        BasicButton(displayText = "OWNED User Profile")
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(Modifier,
                        horizontalArrangement = Arrangement.Center) {
                        BasicButton(Modifier, "OTHER User Profile")
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(Modifier,
                        horizontalArrangement = Arrangement.Center) {
                        BasicButton(Modifier, "Recipe View")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicButton(modifier: Modifier = Modifier, displayText: String) {
    GustoriaTheme {
        Button(
            onClick = {},
            modifier = modifier
        ) {
            Text(displayText, style = MaterialTheme.typography.displaySmall)
        }
    }
}