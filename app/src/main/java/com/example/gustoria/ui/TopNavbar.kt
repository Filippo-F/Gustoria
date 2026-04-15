package com.example.gustoria.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShareNavbar (modifier: Modifier, title: String, onBack: () -> Unit) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        maxItemsInEachRow = 3,
    ) {
        // Back Icon
        IconButton(
            modifier = Modifier.size(56.dp),
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        // Page name
        Box(
            modifier = Modifier.height(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(title, fontSize = 20.sp)
        }

        IconButton(
            modifier = Modifier.size(56.dp),
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share"
            )
        }
    }
}