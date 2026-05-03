package com.example.gustoria.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NavDestination {
    EXPLORE,        // Public list RECIPES_LIST
    CREATE,         // CREATE_RECIPE
    PROFILE         // MY_RECIPES
}

@Composable
fun AppBottomNavBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
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
            icon = Icons.Filled.AddCircle,
            label = "CREATE",
            selected = currentDestination == NavDestination.CREATE,
            isCreate = true,
            onClick = { onNavigate(NavDestination.CREATE) }
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