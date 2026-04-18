package com.example.gustoria

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gustoria.ui.ShareNavbar
import com.example.gustoria.ui.theme.GustoriaTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.HorizontalDivider
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import com.example.gustoria.ui.AppBottomNavBar
import com.example.gustoria.ui.NavDestination

@Preview(name = "Portrait", showSystemUi = true)
@Composable
fun OtherProfileScreenPortrait() {
    GustoriaTheme(dynamicColor = false) {
        OtherProfileScreen(
            viewModel = viewModel(),
            onBack = {},
            onNavigate = {}
        )
    }
}

@Preview(name = "Landscape", showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
fun OtherProfileScreenLandscape() {
    GustoriaTheme(dynamicColor = false) {
        OtherProfileScreen(
            viewModel = viewModel(),
            onBack = {}
        )
    }
}

@Composable
fun ProfileImage(image_url: String?) {
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
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape) // width, color, shape
                .clip(CircleShape)
        )
    }
}

@Composable
fun ProfileInfo(fullName: String, nickname: String, cookingRole: CookingRole?, description: String){
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Full name and nickname
        Text(
            text = fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = buildString {
                append(nickname)
                cookingRole?.let {
                    append(" • ")
                    append(it.displayName())
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
fun ValueBox (
    value: Int,
    text: String,
) {
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)).width(100.dp).height(70.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = value.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(text = text.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CollectionCard(
    collection: UserCollection
) {
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

@Composable
fun OtherProfileScreen(viewModel: OtherProfileViewModel, onBack: () -> Unit = {}, onNavigate: (NavDestination) -> Unit = {}) {
    val tabs = listOf("Collections", "Recent Activity")

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
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Navigation Bar
            ShareNavbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.background),
                title = "Other Profile",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item { ProfileImage(image_url = viewModel.user.profileImageUri) }

                item {
                    ProfileInfo(
                        fullName = viewModel.user.fullName,
                        nickname = viewModel.user.nickname,
                        cookingRole = viewModel.user.cookingRole,
                        description = viewModel.user.description
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
                                containerColor = if (viewModel.isFollowing) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (viewModel.isFollowing) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = if (viewModel.isFollowing) "Unfollow" else "Follow")
                        }
                    }
                }


                item {
                    FlowRow(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        maxItemsInEachRow = 3,
                    ) {
                        ValueBox(viewModel.user.numberOfRecipes, "Recipes")
                        ValueBox(viewModel.user.numberOfFollowers, "Followers")
                        ValueBox(viewModel.user.numberOfLikes, "Likes")
                    }
                }

                // Implementation of the tabs
                item {
                    SecondaryTabRow(
                        selectedTabIndex = viewModel.currentTab,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = viewModel.currentTab == index,
                                onClick = { viewModel.changeTab(index) },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (viewModel.currentTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (viewModel.currentTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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