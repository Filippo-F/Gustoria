package com.example.gustoria.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import com.example.gustoria.ui.ThreeItemTopNavbar
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.gustoria.viewmodel.OtherProfileViewModel
import com.example.gustoria.viewmodel.UserCollection
import com.example.gustoria.viewmodel.UserActivity
import com.example.gustoria.dataclass.CookingRole
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview

@MultiPreview
@Composable
fun OtherProfileScreenPreview() {
    val fakeRepo = com.example.gustoria.ui.utils.PreviewUtils.createFakeUserRepo()
    GustoriaTheme(dynamicColor = false) {
        OtherProfileScreen(
            userRepo = fakeRepo,
            viewedUserId = "101",
            onBack = {},
        )
    }
}

@Composable
fun ProfileImage(imageUrl: String?, fullName: String) {
    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            coil.compose.AsyncImage(
                model = imageUrl,
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
                val initials = fullName
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
}

@Composable
fun ProfileInfo(fullName: String, nickname: String, cookingRole: CookingRole, description: String){
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Full name
        Text(
            text = fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        //nickname + cookingRole
        Text(
            text = buildString {
                append(nickname)
                if (cookingRole != CookingRole.NONE) {
                    append(" • ")
                    append(cookingRole.displayName())
                }
            },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )


        // Description
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }
}

@Composable
fun ValueBox(
    value: Int,
    text: String,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .width(100.dp)
            .height(70.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CollectionCard(collection: UserCollection) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = collection.title,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = collection.subtitle,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CollectionsSection(collections: List<UserCollection>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Curated Collections",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "VIEW ALL",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            collections.forEach { collection ->
                CollectionCard(collection = collection)
            }
        }
    }
}

@Composable
fun ActivityCard(
    activity: UserActivity
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = activity.title,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = activity.subtitle,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RecentActivitySection(activities: List<UserActivity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            activities.forEach { activity ->
                ActivityCard(activity = activity)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OtherProfileScreen(
    userRepo: com.example.gustoria.domain.UserRepoInterface,
    viewedUserId: String,
    onBack: () -> Unit = {},
    viewModel: OtherProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = OtherProfileViewModel.factory(userRepo, viewedUserId)
    )
) {
    val tabs = listOf("Collections", "Recent Activity")
    val user by viewModel.user.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                /* AppBottomNavBar gestita in MainActivity!! */
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            ThreeItemTopNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.background),
                title = "Other Profile",
                onBack = onBack,
                extraIcon = Icons.Default.Share,
                extraIconDescription = "Share",
                onClickExtra = {}
            )

            // Stato di caricamento: utente non ancora emesso dal repo
            val u = user
            if (u == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    ProfileImage(
                        imageUrl = u.profileImageUri,
                        fullName = u.fullName
                    )
                }

                item {
                    ProfileInfo(
                        fullName = u.fullName,
                        nickname = u.nickname,
                        cookingRole = u.cookingRole,
                        description = u.description
                    )
                }

                item {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = viewModel::toggleFollow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isFollowing)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (viewModel.isFollowing)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = if (viewModel.isFollowing) "Unfollow" else "Follow")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ValueBox(u.numberOfRecipes, "Recipes")
                        ValueBox(u.numberOfFollowers, "Followers")
                        ValueBox(u.numberOfLikes, "Likes")
                    }
                }


                item {
                    SecondaryTabRow(
                        selectedTabIndex = viewModel.currentTab,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        divider = {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        },
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = viewModel.currentTab == index,
                                onClick = { viewModel.changeTab(index) },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (viewModel.currentTab == index)
                                            FontWeight.Bold
                                        else
                                            FontWeight.Normal,
                                        color = if (viewModel.currentTab == index)
                                            MaterialTheme.colorScheme.secondary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }

                item {
                    when (viewModel.currentTab) {
                        0 -> CollectionsSection(collections = viewModel.collections)
                        1 -> RecentActivitySection(activities = viewModel.recentActivities)
                    }
                }
            }
        }
    }
}