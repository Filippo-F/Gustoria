package com.example.gustoria.ui.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Returns a human-readable relative time label from a Unix epoch milliseconds timestamp.
// Examples: "Just now", "5m ago", "3h ago", "Yesterday", "Jun 12"

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000        -> "Just now"
        diff < 3_600_000     -> "${diff / 60_000}m ago"
        diff < 86_400_000    -> "${diff / 3_600_000}h ago"
        diff < 172_800_000   -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
