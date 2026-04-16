package com.example.gustoria

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

enum class NavDestination {
    EXPLORE, SEARCH, CREATE, FAVORITES, PROFILE
}

@Composable
fun AppBottomNavBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(72.dp)
    ) {
        NavItem(
            icon = Icons.Outlined.Explore,
            label = "EXPLORE",
            selected = currentDestination == NavDestination.EXPLORE,
            isCreate = false,
            onClick = { onNavigate(NavDestination.EXPLORE) }
        )
        NavItem(
            icon = Icons.Outlined.Search,
            label = "SEARCH",
            selected = currentDestination == NavDestination.SEARCH,
            isCreate = false,
            onClick = { onNavigate(NavDestination.SEARCH) }
        )
        NavItem(
            icon = Icons.Filled.AddCircle,
            label = "CREATE",
            selected = currentDestination == NavDestination.CREATE,
            isCreate = true,
            onClick = { onNavigate(NavDestination.CREATE) }
        )
        NavItem(
            icon = Icons.Filled.Favorite,
            label = "FAVORITES",
            selected = currentDestination == NavDestination.FAVORITES,
            isCreate = false,
            onClick = { onNavigate(NavDestination.FAVORITES) }
        )
        NavItem(
            icon = Icons.Outlined.Person,
            label = "PROFILE",
            selected = currentDestination == NavDestination.PROFILE,
            isCreate = false,
            onClick = { onNavigate(NavDestination.PROFILE) }
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
    val createColor = Color(0xFFB84A2E)   // rosso-arancio
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = Color(0xFF9E9E9E)

    val iconTint = when {
        isCreate -> createColor
        selected -> activeColor
        else -> inactiveColor
    }
    val textColor = when {
        isCreate -> createColor
        selected -> activeColor
        else -> inactiveColor
    }

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