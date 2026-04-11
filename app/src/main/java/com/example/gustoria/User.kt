package com.example.gustoria

data class User(
    val firstName: String = "John",
    val lastName: String = "Doe",
    val username: String = "johndoe",
    val email: String = "john.doe@example.com",
    val bio: String = "I love cooking and exploring new recipes!",
    val profilePictureUrl: String? = null,
    val location: String? = "New York, USA",
    val internalID: Int = 101
) {
}
