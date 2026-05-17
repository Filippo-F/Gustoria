package com.example.gustoria.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThreeItemTopNavbar(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit = {},
    showBackButton: Boolean = true,
    extraIcon: ImageVector? = null,
    extraIconDescription: String? = null,
    onClickExtra: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            IconButton(
                modifier = Modifier.size(56.dp),
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        } else {
            Box(modifier = Modifier.size(56.dp))
        }

        Text(
            text = title,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )

        if (extraIcon != null) {
            IconButton(
                modifier = Modifier.size(56.dp),
                onClick = onClickExtra
            ) {
                Icon(
                    imageVector = extraIcon,
                    contentDescription = extraIconDescription
                )
            }
        } else {
            Box(modifier = Modifier.size(56.dp))
        }
    }
}

/*

variante usata dalla HomeScreen con avatar a sinistra, notifiche a destra e nome app centrale

 - leadingContent: Composable sinistra (avatar)
 - trailingContent: Composable destra (notifiche)
 - title: testo centrato (nome app) 
 
*/

@Composable
fun ThreeItemTopNavbar(
    modifier: Modifier = Modifier,
    titleContent: @Composable () -> Unit,
    leadingContent: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit = { Box(modifier = Modifier.size(56.dp)) }
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent()

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            titleContent()
        }

        trailingContent()
    }
}