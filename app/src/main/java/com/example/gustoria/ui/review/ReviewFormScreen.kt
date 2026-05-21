package com.example.gustoria.ui.review

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.gustoria.SessionManager
import com.example.gustoria.dataclass.Review
import com.example.gustoria.domain.ReviewRepoInterface
import com.example.gustoria.ui.CameraXScreen
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.viewmodel.ReviewViewModel

class ReviewFormActions(private val navController: NavHostController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewFormScreen(
    recipeId: String,
    navController: NavHostController,
    viewModel: ReviewViewModel = viewModel(factory = ReviewViewModel.Factory)
) {
    var rating by remember { mutableStateOf(5f) }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf("") }

    val context = LocalContext.current
    val showImageMenu = remember { mutableStateOf(false) }
    val showCameraScreen = remember { mutableStateOf(false) }
    val actions = remember(navController) { ReviewFormActions(navController) }

    // image selection and camera permissions
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) photoUri = uri.toString()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showCameraScreen.value = true
        else Toast.makeText(context, "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
    }

    //show camera screen (if requested) or show the form
    if (showCameraScreen.value) {
        Dialog(
            onDismissRequest = { showCameraScreen.value = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            CameraXScreen(
                onImageCaptured = { uriString ->
                    photoUri = uriString
                    showCameraScreen.value = false
                },
                onCancel = { showCameraScreen.value = false }
            )
        }
    } else {
        Scaffold(
            topBar = {
                ThreeItemTopNavbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    title = "Write a review",
                    onBack = actions.navigateBack
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text("Rating", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = (index + 1).toFloat() }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                //image preview and upload button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri.isNotBlank()) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Review image preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "No photo",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    //add/change photo
                    IconButton(
                        onClick = { showImageMenu.value = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Photo",
                            tint = Color.White
                        )
                    }

                    //dropdown for camera vs gallery
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
                                    context, Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (isGranted) showCameraScreen.value = true
                                else permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Write your review and tips (DOs and DON'Ts)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val review = Review(
                            userId = SessionManager.CURRENT_LOGGED_IN_USER_ID,
                            recipeId = recipeId,
                            description = description,
                            rating = rating,
                            photoUri = photoUri.ifBlank { null },
                            timestamp = System.currentTimeMillis().toString()
                        )
                        viewModel.addReview(review)
                        actions.navigateBack()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("POST")
                }
            }
        }
    }
}