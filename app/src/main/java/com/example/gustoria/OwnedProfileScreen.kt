package com.example.gustoria

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.AppBottomNavBar
import com.example.gustoria.ui.NavDestination
import com.example.gustoria.ui.theme.GustoriaTheme
import java.io.File

@Preview(name = "Portrait", showSystemUi = true)
@Composable
fun OwnedProfileScreenPreviewPortrait() {
    GustoriaTheme(dynamicColor = false) {
        OwnedProfileScreen(
            viewModel = viewModel(),
            onBack = {},
            onNavigate = {}
        )
    }
}

@Preview(name = "Landscape", widthDp = 851, heightDp = 393, showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
fun OwnedProfileScreenPreviewLandscape() {
    GustoriaTheme(dynamicColor = false) {
        OwnedProfileScreen(
            viewModel = viewModel(),
            onBack = {},
            onNavigate = {}
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OwnedProfileScreen(viewModel: OwnedProfileViewModel, onBack: () -> Unit = {}, onNavigate: (NavDestination) -> Unit) {
    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                AppBottomNavBar(
                    currentDestination = NavDestination.PROFILE,
                    onNavigate = onNavigate
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.width(56.dp),
                    onClick = {
                        if (viewModel.isEditing) {
                            viewModel.validateAndSave()
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Text("Profile Page", fontSize = 20.sp)

                if (!viewModel.isEditing) {
                    IconButton(
                        modifier = Modifier.width(56.dp),
                        onClick = { viewModel.startEditing() }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
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
                    onCookingRoleChange = viewModel::setCookingRole,
                    onCuisinePreferencesChange = viewModel::setCuisinePreferences,
                    onDietaryRestrictionsChange = viewModel::setDietaryRestrictions,
                    onFavoriteIngredientsChange = viewModel::setFavoriteIngredients,
                    onImageChange = viewModel::setProfileImageUri,
                    onSave = viewModel::validateAndSave,
                    onCancel = viewModel::cancelEditing
                )
            } else {
                PresentationPane(user = viewModel.user)
            }
        }
    }
}

@Composable
fun PresentationPane(user: UserClass) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                ImageBoxContent(user)
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${user.fullName} (${user.nickname})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.description.ifBlank { "No description yet." },
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }

        @OptIn(ExperimentalLayoutApi::class)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("🌿 Vegan Specialist", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("⭐ Top Curator", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        @OptIn(ExperimentalLayoutApi::class)
        item {
            FlowRow(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                maxItemsInEachRow = 3,
            ) {
                ValueBox(value = user.numberOfRecipes, text = "Recipes")
                ValueBox(value = user.numberOfFollowers, text = "Followers")
                ValueBox(value = user.numberOfLikes, text = "Likes")
            }
        }

        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp)) {
                MenuListItem(title = "Profile Info", icon = Icons.Default.Person)
                MenuListItem(title = "Settings", icon = Icons.Default.Settings)
                MenuListItem(title = "Help & Feedback", icon = Icons.Default.Info)
                Spacer(modifier = Modifier.height(16.dp))
                MenuListItem(title = "Sign Out", icon = Icons.AutoMirrored.Filled.ExitToApp, isDestructive = true)
            }
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
    onCookingRoleChange: (CookingRole?) -> Unit,
    onCuisinePreferencesChange: (List<String>) -> Unit,
    onDietaryRestrictionsChange: (List<String>) -> Unit,
    onFavoriteIngredientsChange: (List<String>) -> Unit,
    onImageChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val showImageMenu = remember { mutableStateOf(false) }
    val showCameraScreen = remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onImageChange(uri.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraScreen.value = true
        } else {
            Toast.makeText(context, "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    if (showCameraScreen.value) {
        CameraXScreen(
            onImageCaptured = { uriString ->
                onImageChange(uriString)
                showCameraScreen.value = false
            },
            onCancel = { showCameraScreen.value = false }
        )
    } else {
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
                    ImageBoxContent(user)

                    Box(
                        modifier = Modifier.size(120.dp)
                    ) {
                        IconButton(
                            onClick = { showImageMenu.value = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Picture",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showImageMenu.value,
                            onDismissRequest = { showImageMenu.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Select from Gallery") },
                                onClick = {
                                    showImageMenu.value = false
                                    galleryLauncher.launch("image/*")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Take a Picture") },
                                onClick = {
                                    showImageMenu.value = false
                                    val isGranted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (isGranted) {
                                        showCameraScreen.value = true
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            )
                        }
                    }
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
                    value = user.email,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                OutlinedTextField(
                    value = user.cuisinePreferences.joinToString(", "),
                    onValueChange = onCuisinePreferencesChange,
                    label = { Text("Cuisine Preferences") },
                    supportingText = { Text("Separate values with commas") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = user.dietaryRestrictions.joinToString(", "),
                    onValueChange = onDietaryRestrictionsChange,
                    label = { Text("Dietary Restrictions") },
                    supportingText = { Text("Separate values with commas") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = user.favoriteIngredients.joinToString(", "),
                    onValueChange = onFavoriteIngredientsChange,
                    label = { Text("Favorite Ingredients") },
                    supportingText = { Text("Separate values with commas") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
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
}

@Composable
fun MenuListItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isDestructive: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(56.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDestructive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = if (isDestructive) MaterialTheme.colorScheme.error else Color.DarkGray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Medium, color = if (isDestructive) MaterialTheme.colorScheme.error else Color.Black)
        }
    }
}

@Composable
fun ImageBoxContent(user: UserClass) {
    if (user.profileImageUri != null) {
        coil.compose.AsyncImage(
            model = user.profileImageUri,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            val initials = user.fullName
                .split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()

            Text(
                text = initials,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CameraXScreen(
    onImageCaptured: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    // THIS IS THE EMULATOR BLACK SCREEN FIX!
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                val photoFile = File(context.cacheDir, "profile_pic_${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                cameraController.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            onImageCaptured(photoFile.toURI().toString())
                        }
                        override fun onError(exc: ImageCaptureException) {
                            println("Photo capture failed: ${exc.message}")
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Snap")
        }

        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}