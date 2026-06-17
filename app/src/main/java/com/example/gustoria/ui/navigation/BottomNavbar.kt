package com.example.gustoria.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavBar(
    navCtrl: NavHostController,
    navActions: GustoriaNavigationActions
) {
    val currentBackStackEntry by navCtrl.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp), 
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomNavItem(
                    selectedIcon = Icons.Filled.Explore,
                    unselectedIcon = Icons.Outlined.Explore,
                    label = "Explore",
                    selected = currentDestination?.hasRoute<Home>() == true,
                    onClick = navActions::navigateToHome
                )
                CustomNavItem(
                    selectedIcon = Icons.Filled.Search,
                    unselectedIcon = Icons.Outlined.Search,
                    label = "Search",
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<Search>() } == true,
                    onClick = navActions::navigateToSearch
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = navActions::navigateToCreateRecipe
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                CustomNavItem(
                    selectedIcon = Icons.Filled.Favorite,
                    unselectedIcon = Icons.Outlined.FavoriteBorder,
                    label = "Favorites",
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<Favourite>() } == true,
                    onClick = navActions::navigateToFavouriteSaved
                )
                CustomNavItem(
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    label = "Profile",
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<Profile>() } == true,
                    onClick = navActions::navigateToProfile
                )
            }
        }
    }
}

@Composable
private fun RowScope.CustomNavItem(
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box con altezza fissa per forzare l'allineamento perfetto delle icone
        Box(
            modifier = Modifier.height(26.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
