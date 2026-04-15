package com.example.gustoria

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight

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

@Preview(name = "Landscape", widthDp = 851, heightDp = 393, showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Profile Image OR Monogram if image is missing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace with actual image URI check from UserClass
                val hasImage = true

                if (hasImage) {
                    Image(
                        painter = painterResource(id = R.drawable.guest_user_profile_pic), // Placeholder
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clip(CircleShape)
                    )
                } else {
                    // MONOGRAM
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.take(1).uppercase(), // Takes first letter
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            // Profile Info (Name & Role)
            Column(
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${user.fullName} (${user.nickname})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Description
                Text(
                    text = user.description,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }

        // PREFERENCES / TAGS
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tag 1
                Box(modifier = Modifier.background(Color(0xFFFDECE8), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("🌿 Vegan Specialist", color = Color(0xFFA0522D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Tag 2
                Box(modifier = Modifier.background(Color(0xFFE8F8F5), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("⭐ Top Curator", color = Color(0xFF0E6655), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // STATS BOXES (Similar to OtherProfile)
        item {
            FlowRow(
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                maxItemsInEachRow = 3,
            ) {
                // Placeholder numbers
                ValueBox(value = 42, text = "Recipes")
                ValueBox(value = 1200, text = "Followers")
                ValueBox(value = 850, text = "Likes")
            }
        }

        // SETTINGS MENU LIST
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp)) {
                MenuListItem(title = "Profile Info", icon = Icons.Default.Person)
                MenuListItem(title = "Settings", icon = Icons.Default.Settings)
                MenuListItem(title = "Help & Feedback", icon = Icons.Default.Info)
                Spacer(modifier = Modifier.height(16.dp))
                MenuListItem(title = "Sign Out", icon = Icons.AutoMirrored.Filled.ExitToApp, isDestructive = true)
            }
        }
    }
}

/**
 * Helper component for the settings list at the bottom of the profile
 */
@Composable
fun MenuListItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isDestructive: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(56.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDestructive) Color(0xFFFDECE8) else Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDestructive) Color.Red else Color.LightGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = if (isDestructive) Color.Red else Color.DarkGray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Medium, color = if (isDestructive) Color.Red else Color.Black)
        }
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
