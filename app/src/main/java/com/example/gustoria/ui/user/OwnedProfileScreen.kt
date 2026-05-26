package com.example.gustoria.ui.user

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.ui.navigation.Profile
import com.example.gustoria.dataclass.User
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.viewmodel.OwnedProfileViewModel

@MultiPreview
@Composable
fun OwnedProfileScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        OwnedProfileScreen(
            user = null,
            recipeCount = 0,
            onBack = {},
            onNavigateToProfileInfo = {},
            onNavigateToSettings = {},
            onNavigateToHelp = {},
            onSignOut = {}
        )
    }
}

@Composable
fun OwnedProfileScreen(
    user: User?,
    recipeCount: Int,
    isLoggedIn: Boolean = true,
    onBack: () -> Unit,
    onNavigateToProfileInfo: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onSignOut: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = "My Profile",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
                return@Box
            }

            PresentationPane(
                user = user,
                recipeCount = recipeCount,
                isLandscape = isLandscape,
                isLoggedIn = isLoggedIn,
                onNavigateToProfileInfo = onNavigateToProfileInfo,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToHelp = onNavigateToHelp,
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
fun PresentationPane(
    user: User,
    recipeCount: Int,
    isLandscape: Boolean,
    isLoggedIn: Boolean = true,
    onNavigateToProfileInfo: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLandscape) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageBoxContent(user)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "${user.fullName} (${user.nickname})",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.description.ifBlank { "No description yet." },
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) { ImageBoxContent(user) }
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
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🌿 Vegan Specialist",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⭐ Top Curator",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileValueBox(recipeCount, "Recipes")
                ProfileValueBox(user.numberOfFollowers, "Followers")
                ProfileValueBox(user.numberOfLikes, "Likes")
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                MenuListItem(title = "Profile Info", icon = Icons.Default.Person, onClick = onNavigateToProfileInfo)
                MenuListItem(title = "Settings", icon = Icons.Default.Settings, onClick = onNavigateToSettings)
                MenuListItem(title = "Help & Feedback", icon = Icons.Default.Info, onClick = onNavigateToHelp)
                if (isLoggedIn) {
                    Spacer(modifier = Modifier.height(16.dp))
                    MenuListItem(
                        title = "Sign Out",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        isDestructive = true,
                        onClick = onSignOut
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileValueBox(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MenuListItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(56.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isDestructive) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ImageBoxContent(user: User) {
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
                .filter { it.isNotBlank() }
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()

            Text(
                text = initials,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
