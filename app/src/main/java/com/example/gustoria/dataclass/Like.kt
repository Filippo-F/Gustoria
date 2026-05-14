package com.example.gustoria.dataclass

enum class LikeTarget {
    NONE,
    RECIPE,
    REVIEW,
}

data class Like(
    val userId: String = "",
    val targetId: String = "",

    val targetType: LikeTarget = LikeTarget.NONE,

    val createdAt: String = ""
)
