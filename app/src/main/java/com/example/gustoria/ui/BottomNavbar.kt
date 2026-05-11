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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gustoria.Create
import com.example.gustoria.Favourite
import com.example.gustoria.Favourite.Saved
import com.example.gustoria.Home
import com.example.gustoria.Profile
import com.example.gustoria.Search

class BottomNavBarActions (
    val navCtrl : NavHostController
) {
    val navigateHome: () -> Unit = {
        navCtrl.navigate(Home)
    }
    val navigateSearch: () -> Unit = {
        navCtrl.navigate(Search)
    }
    val navigateCreate: () -> Unit = {
        navCtrl.navigate(Create)
    }
    val navigateFavourite: () -> Unit = {
        navCtrl.navigate(Saved)
    }
    val navigateProfile: () -> Unit = {
        navCtrl.navigate(Profile)
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
            selected = currentDestination?.hasRoute<Home>() == true,
            isCreate = false,
            onClick = actions.navigateHome
        )
        NavItem(
            icon = Icons.Outlined.Search,
            label = "SEARCH",
            selected = currentDestination?.hierarchy?.any { it.hasRoute<Search>() } == true,
            isCreate = false,
            onClick = actions.navigateSearch
        )
        NavItem(
            icon = Icons.Filled.AddCircle,
            label = "CREATE",
            selected = currentDestination?.hasRoute<Create>() == true,
            isCreate = true,
            onClick = actions.navigateCreate
        )
        NavItem(
            icon = Icons.Filled.Favorite,
            label = "FAVORITES",
            selected = currentDestination?.hierarchy?.any { it.hasRoute<Favourite>() } == true,
            isCreate = false,
            onClick = actions.navigateFavourite
        )
        NavItem(
            icon = Icons.Outlined.Person,
            label = "PROFILE",
            selected = currentDestination?.hierarchy?.any { it.hasRoute<Profile>() } == true,
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