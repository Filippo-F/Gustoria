package com.example.gustoria.ui.user

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gustoria.ui.ThreeItemTopNavbar
import com.example.gustoria.ui.theme.GustoriaTheme
import com.example.gustoria.ui.utils.MultiPreview
import com.example.gustoria.viewmodel.SettingsViewModel

@MultiPreview
@Preview
@Composable
fun SettingsScreenPreview() {
    GustoriaTheme(dynamicColor = false) {
        SettingsScreen(
            navController = rememberNavController()
        )
    }
}

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            ThreeItemTopNavbar(
                title = "Settings",
                onBack = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
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
            AppearanceAndDisplaySection(viewModel)
            Spacer(modifier = Modifier.height(32.dp))
            NotificationsSection(viewModel)
            Spacer(modifier = Modifier.height(32.dp))
            AppPreferenceSection(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceAndDisplaySection(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Macro section title
        Text(
            text = "Appearance & Display",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Theme selection
        SettingItem(
            title = "Visual Theme",
            description = "Choose between light, dark or system default theme"
        ) {
            val themes = listOf("Light", "Dark", "Auto")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                themes.forEachIndexed { index, label ->
                    FilterChip(
                        selected = viewModel.selectedTheme == index,
                        onClick = { viewModel.updateTheme(index) },
                        label = {
                            Text(
                                text = label,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Brightness bar
        SettingItem(
            title = "Brightness",
            description = "Adjust the screen brightness level"
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BrightnessLow, contentDescription = "Low Brightness", modifier = Modifier.size(20.dp))
                Slider(
                    value = viewModel.brightness,
                    onValueChange = { viewModel.updateBrightness(it) },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            thumbSize = DpSize(12.dp, 12.dp),
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                )
                Icon(Icons.Default.BrightnessHigh, contentDescription = "High Brightness", modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Font size bar
        SettingItem(
            title = "Font size",
            description = "Make the text smaller or bigger"
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("A", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                Slider(
                    value = viewModel.fontSize,
                    onValueChange = { viewModel.updateFontSize(it) },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            thumbSize = DpSize(12.dp, 12.dp),
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                )
                Text("A", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 4.dp))
            }
        }
    }
}

@Composable
fun NotificationsSection(viewModel: SettingsViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        NotificationToggleItem(
            title = "Push notification",
            description = "Receive instant updates on your device",
            icon = Icons.Default.Notifications,
            checked = viewModel.pushNotificationsEnabled,
            onCheckedChange = { viewModel.togglePushNotifications() }
        )
        Spacer(modifier = Modifier.height(12.dp))
        NotificationToggleItem(
            title = "New recipe alerts",
            description = "Get notified when new recipes are added",
            icon = Icons.Default.Restaurant,
            checked = viewModel.newRecipeAlertsEnabled,
            onCheckedChange = { viewModel.toggleNewRecipeAlerts() }
        )
        Spacer(modifier = Modifier.height(12.dp))
        NotificationToggleItem(
            title = "Gustoria weekly",
            description = "Weekly digest of the best recipes and news",
            icon = Icons.Default.Email,
            checked = viewModel.gustoriaWeeklyEnabled,
            onCheckedChange = { viewModel.toggleGustoriaWeekly() }
        )
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.secondary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPreferenceSection(viewModel: SettingsViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "App Preference",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(16.dp))

        SettingItem(
            title = "Unit measure",
            description = "Choose between metric and imperial units",
            titleColor = MaterialTheme.colorScheme.tertiary
        ) {
            val units = listOf("Metric", "Imperial")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                units.forEachIndexed { index, label ->
                    FilterChip(
                        selected = viewModel.unitMeasure == index,
                        onClick = { viewModel.updateUnitMeasure(index) },
                        label = {
                            Text(
                                text = label,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = titleColor
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
}
