package com.example.gustoria

data class UserProfile(
    val fullName: String,
    val nickname: String,
    val email: String,
    val description: String,
    val profileImageUri: String? = null, //per ora
    val internalID: Int = 101,
)
