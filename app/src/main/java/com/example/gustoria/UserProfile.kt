package com.example.gustoria

data class UserProfile(
    val id: String,
    val fullName: String,
    val nickname: String,
    val email: String,
    val description: String,
    val profileImageUri: String? = null //per ora
)
