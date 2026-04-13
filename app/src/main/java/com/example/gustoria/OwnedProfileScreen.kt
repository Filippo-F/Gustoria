package com.example.gustoria

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun OwnedProfileScreenPreview () {
    MaterialTheme {
        OwnedProfileScreen(
            viewModel = OwnedProfileViewModel(),
            onBack = {}
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OwnedProfileScreen(viewModel: OwnedProfileViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            maxItemsInEachRow = 3,
        ) {
            // Back Icon
            IconButton(
                modifier = Modifier.width(56.dp),
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            // Page name
            Box (
                modifier = Modifier.height(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Profile Page", fontSize = 20.sp)
            }

            // Edit Button
            IconButton(
                modifier = Modifier.width(56.dp),
                onClick = { /* TODO: handle edit */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }
        }

        // Profile Image
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.guest_user_profile_pic),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop, // crops to fill the circle
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, Color.Gray, CircleShape) // width, color, shape
                    .clip(CircleShape)
            )
        }

        // Profile Info
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Full Name and Nickname
            Text("${viewModel.user.fullName} (${viewModel.user.nickname})", fontSize = 20.sp)

            // Description
            Text(
                text = viewModel.user.description,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Preferences
        // TODO: implement preferences
    }
}
