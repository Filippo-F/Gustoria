package com.example.gustoria.ui.user

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringArrayResource
import com.example.gustoria.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gustoria.dataclass.CookingRole
import com.example.gustoria.dataclass.User
import com.example.gustoria.ui.CameraXScreen
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.viewmodel.OwnedProfileViewModel

@MultiPreview
@Preview
@Composable
fun ProfileInfoScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        ProfileInfoScreen(
            user = null,
            viewModel = viewModel(factory = OwnedProfileViewModel.Factory),
            onBack = {},
            onSave = {}
        )
    }
}

@Composable
fun ProfileInfoScreen(
    user: User?,
    viewModel: OwnedProfileViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    // Initialize editing draft when user data is loaded
    LaunchedEffect(user) {
        if (viewModel.editableUser == null && user != null) {
            viewModel.startEditing()
        }
    }

    val draft = viewModel.editableUser
    if (draft == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val context = LocalContext.current
    val showImageMenu = remember { mutableStateOf(false) }
    val showCameraScreen = remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.setProfileImageUri(uri.toString())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showCameraScreen.value = true
        else Toast.makeText(context, "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
    }

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
                    viewModel.setProfileImageUri(uriString)
                    showCameraScreen.value = false
                },
                onCancel = { showCameraScreen.value = false }
            )
        }
    } else {
        Scaffold(
            topBar = {
                ThreeItemTopNavbar(
                    title = "Profile Info",
                    onBack = onBack
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                //Header: photo and nickname
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        ImageBoxContent(draft)
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
                                tint = MaterialTheme.colorScheme.onPrimary,
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
                                        context, Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (isGranted) showCameraScreen.value = true
                                    else permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = draft.nickname,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Public profile information",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Account information
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Nickname
                AccountInfoItem(
                    label = "Nickname",
                    value = draft.nickname,
                    isEditing = viewModel.editingNickname,
                    onEditToggle = { viewModel.toggleEditingNickname() },
                    onValueChange = { viewModel.setNickname(it) },
                    error = viewModel.validation.nicknameError
                )

                // Name
                AccountInfoItem(
                    label = "Name",
                    value = draft.firstName,
                    isEditing = viewModel.editingFirstName,
                    onEditToggle = { viewModel.toggleEditingFirstName() },
                    onValueChange = { viewModel.setFirstName(it) },
                    error = viewModel.validation.firstNameError
                )

                // Surname
                AccountInfoItem(
                    label = "Surname",
                    value = draft.lastName,
                    isEditing = viewModel.editingLastName,
                    onEditToggle = { viewModel.toggleEditingLastName() },
                    onValueChange = { viewModel.setLastName(it) },
                    error = viewModel.validation.lastNameError
                )

                // Phone number
                AccountInfoItem(
                    label = "Phone Number",
                    value = draft.phoneNumber,
                    isEditing = viewModel.editingPhone,
                    onEditToggle = { viewModel.toggleEditingPhone() },
                    onValueChange = { viewModel.setPhoneNumber(it) },
                    error = viewModel.validation.phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Culinary preference section
                Text(
                    text = "Culinary Preference",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                InfoSectionItem(
                    title = "Favorite Cuisine",
                    description = "Your favorite types of food (comma separated)",
                    titleColor = MaterialTheme.colorScheme.secondary
                ) {
                    OutlinedTextField(
                        value = viewModel.cuisinePreferencesText,
                        onValueChange = { viewModel.setCuisinePreferencesFromText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("e.g. Italian, Japanese, Mexican") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                            cursorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoSectionItem(
                    title = "Diet Preference",
                    description = "Any dietary restrictions or preferences (comma separated)",
                    titleColor = MaterialTheme.colorScheme.secondary
                ) {
                    OutlinedTextField(
                        value = viewModel.dietaryRestrictionsText,
                        onValueChange = { viewModel.setDietaryRestrictionsFromText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("e.g. Vegan, Gluten-free") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                            cursorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoSectionItem(
                    title = "Favorite Ingredients",
                    description = "Ingredients you love to use (comma separated)",
                    titleColor = MaterialTheme.colorScheme.secondary
                ) {
                    OutlinedTextField(
                        value = viewModel.favoriteIngredientsText,
                        onValueChange = { viewModel.setFavoriteIngredientsFromText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("e.g. Garlic, Basil, Olive Oil") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                            cursorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Public profile section
                Text(
                    text = "Public Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                InfoSectionItem(
                    title = "Cooking Role",
                    description = "Your expertise level",
                    isError = viewModel.validation.cookingRoleError.isNotBlank(),
                    titleColor = MaterialTheme.colorScheme.tertiary
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    val cookingRoles = stringArrayResource(R.array.cooking_roles)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (draft.cookingRole == CookingRole.NONE) "" else cookingRoles[draft.cookingRole.ordinal],
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                                        else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                }
                            },
                            isError = viewModel.validation.cookingRoleError.isNotBlank(),
                            supportingText = if (viewModel.validation.cookingRoleError.isNotBlank()) {
                                { Text(viewModel.validation.cookingRoleError) }
                            } else null,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                                cursorColor = MaterialTheme.colorScheme.tertiary
                            )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CookingRole.entries.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(cookingRoles[role.ordinal]) },
                                    onClick = {
                                        viewModel.setCookingRole(role)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoSectionItem(
                    title = "Bio",
                    description = "A short story about your culinary journey",
                    isError = viewModel.validation.descriptionError.isNotBlank(),
                    titleColor = MaterialTheme.colorScheme.tertiary
                ) {
                    OutlinedTextField(
                        value = draft.description,
                        onValueChange = { viewModel.setDescription(it) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = viewModel.validation.descriptionError.isNotBlank(),
                        supportingText = if (viewModel.validation.descriptionError.isNotBlank()) {
                            { Text(viewModel.validation.descriptionError) }
                        } else null,
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                            cursorColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Discard")
                    }
                    Button(
                        onClick = {
                            // Pass onSave directly to viewmodel
                            viewModel.validateAndSave(onSuccess = onSave)
                        },
                        enabled = !viewModel.isSubmitting,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (viewModel.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AccountInfoItem(
    label: String,
    value: String,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    onValueChange: (String) -> Unit,
    error: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Surface(
        color = if (error.isNotBlank()) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f) 
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp),
        border = if (error.isNotBlank()) BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (error.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isEditing) {
                        Text(
                            text = value.ifBlank { "Not set" },
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (value.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        if (error.isNotBlank()) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                TextButton(
                    onClick = onEditToggle,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    if (!isEditing) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (error.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (isEditing) "Done" else "Change",
                        color = if (error.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    isError = error.isNotBlank(),
                    supportingText = if (error.isNotBlank()) {
                        { Text(error) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions
                )
            }
        }
    }
}

@Composable
fun InfoSectionItem(
    title: String,
    description: String,
    isError: Boolean = false,
    titleColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isError) MaterialTheme.colorScheme.error else titleColor
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
