package com.example.gustoria.dataclass

// Represents a single recent search entry for a user.

data class RecentSearch(
    val id: String = "",
    val title: String = "",
    val searchedAt: Long = System.currentTimeMillis()
)
