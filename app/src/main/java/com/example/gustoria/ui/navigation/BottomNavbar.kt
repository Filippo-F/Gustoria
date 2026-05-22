package com.example.gustoria.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gustoria.ui.navigation.Create
import com.example.gustoria.ui.navigation.Home

@Composable
fun AppBottomNavBar(
    navCtrl: NavHostController,
    navActions: GustoriaNavigationActions
) {
    val currentBackStackEntry by navCtrl.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.height(72.dp)
    ) {
        NavItem(
            icon = Icons.Outlined.Explore,
            label = "EXPLORE",
            selected = currentDestination?.hasRoute<Home>() == true,
            isCreate = false,
            onClick = navActions::navigateToHome
        )
        NavItem(
            icon = Icons.Outlined.Search,
            label = "SEARCH",
            selected = currentDestination?.hierarchy?.any { it.hasRoute<Search>() } == true,
            isCreate = false,
            onClick = navActions::navigateToSearch
        )
        NavItem(
            icon = Icons.Filled.AddCircle,
            label = "CREATE",
            selected = currentDestination?.hasRoute<Create>() == true,
            isCreate = true,
            onClick = navActions::navigateToCreateRecipe
        )
        NavItem(
            icon = Icons.Filled.Favorite,
            label = "FAVORITES",
            selected = currentDestination?.hierarchy?.any { it.hasRoute<Favourite>() } == true,
            isCreate = false,
            onClick = navActions::navigateToFavouriteSaved
        )
        NavItem(
            icon = Icons.Outlined.Person,
            label = "PROFILE",
            selected = currentDestination?.hierarchy?.any { it.hasRoute<Profile>() } == true,
            isCreate = false,
            onClick = navActions::navigateToProfile
        )
    }
}

@Composable
private fun RowScope.NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    isCreate: Boolean,
    onClick: () -> Unit
) {
    val createColor = MaterialTheme.colorScheme.secondary
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface

    val iconTint = when {
        isCreate -> createColor
        selected -> activeColor
        else -> inactiveColor
    }
    val textColor = iconTint

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        ),
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(if (isCreate) 32.dp else 24.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = if (selected && !isCreate) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    )
}