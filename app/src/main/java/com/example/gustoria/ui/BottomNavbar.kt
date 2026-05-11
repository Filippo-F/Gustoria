package com.example.gustoria.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

class BottomNavBarActions (
    val navCtrl : NavHostController
) {
    val navigateHome: () -> Unit = {
        navCtrl.navigate(HomeRoute)
    }
    val navigateSearch: () -> Unit = {
        navCtrl.navigate(SearchRoute)
    }
    val navigateCreate: () -> Unit = {
        navCtrl.navigate(CreateRoute)
    }
    val navigateFavourite: () -> Unit = {
        navCtrl.navigate(FavouriteRoute)
    }
    val navigateProfile: () -> Unit = {
        navCtrl.navigate(ProfileRoute)
    }
    val navigateBack: () -> Unit = {
        navCtrl.popBackStack()
    }
}

@Composable
fun AppBottomNavBar(
    navCtrl: NavHostController,
) {
    val currentBackStackEntry by navCtrl.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val actions = remember(navCtrl) {
        BottomNavBarActions(navCtrl)
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.height(72.dp)
    ) {
        NavItem(
            icon = Icons.Outlined.Explore,
            label = "EXPLORE",
            selected = currentDestination?.hasRoute<HomeRoute>() == true,
            isCreate = false,
            onClick = actions.navigateHome
        )
        NavItem(
            icon = Icons.Outlined.Search,
            label = "SEARCH",
            selected = currentDestination?.hasRoute<SearchRoute>() == true,
            isCreate = false,
            onClick = actions.navigateSearch
        )
        NavItem(
            icon = Icons.Filled.AddCircle,
            label = "CREATE",
            selected = currentDestination?.hasRoute<CreateRoute>() == true,
            isCreate = true,
            onClick = actions.navigateCreate
        )
        NavItem(
            icon = Icons.Filled.Favorite,
            label = "FAVORITES",
            selected = currentDestination?.hasRoute<FavouriteRoute>() == true,
            isCreate = false,
            onClick = actions.navigateFavourite
        )
        NavItem(
            icon = Icons.Outlined.Person,
            label = "PROFILE",
            selected = currentDestination?.hasRoute<ProfileRoute>() == true,
            isCreate = false,
            onClick = actions.navigateProfile
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