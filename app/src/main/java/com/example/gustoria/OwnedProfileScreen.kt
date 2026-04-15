package com.example.gustoria

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(name = "Portrait", showSystemUi = true)
@Composable
fun OwnedProfileScreenPreviewPortrait() {
    MaterialTheme {
        OwnedProfileScreen(
            viewModel = viewModel(),
            onBack = {}
        )
    }
}

@Preview(name = "Landscape", widthDp = 851, heightDp = 393, showSystemUi = true)
@Composable
fun OwnedProfileScreenPreviewLandscape() {
    MaterialTheme {
        OwnedProfileScreen(
            viewModel = viewModel(),
            onBack = {}
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OwnedProfileScreen(viewModel: OwnedProfileViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
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
            Box(
                modifier = Modifier.height(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Profile Page", fontSize = 20.sp)
            }

            if (!viewModel.isEditing) {
                // Edit Button
                IconButton(
                    modifier = Modifier.width(56.dp),
                    onClick = { viewModel.startEditing() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
            } else {
                Box(modifier = Modifier.width(56.dp))
            }
        }

        if (viewModel.isEditing) {
            EditProfilePane(
                user = viewModel.editableUser,
                validation = viewModel.validation,
                onNicknameChange = viewModel::setNickname,
                onDescriptionChange = viewModel::setDescription,
                onPhoneChange = viewModel::setPhoneNumber,
                onSave = viewModel::validateAndSave,
                onCancel = viewModel::cancelEditing
            )
        } else {
            PresentationPane(user = viewModel.user)
        }
    }
}

@Composable
fun PresentationPane(user: UserClass) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            // Profile Image
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
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
        }

        item {
            // Profile Info
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Full Name and Nickname
                Text("${user.fullName} (${user.nickname})", fontSize = 20.sp)

                // Description
                Text(
                    text = user.description,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Preferences
        // TODO: implement preferences
    }
}

@Composable
fun EditProfilePane(
    user: UserClass,
    validation: ProfileValidation,
    onNicknameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.guest_user_profile_pic),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clip(CircleShape)
                )
            }
        }

        item {
            Text(
                text = user.fullName,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            OutlinedTextField(
                value = user.nickname,
                onValueChange = onNicknameChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("Nickname") },
                isError = validation.nicknameError.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (validation.nicknameError.isNotBlank()) {
            item {
                Text(
                    text = validation.nicknameError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = user.description,
                onValueChange = onDescriptionChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = user.phoneNumber,
                onValueChange = onPhoneChange,
                label = { Text("Phone Number") },
                isError = validation.phoneError.isNotBlank(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }

        if (validation.phoneError.isNotBlank()) {
            item {
                Text(
                    text = validation.phoneError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}
