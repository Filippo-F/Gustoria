package com.example.gustoria.dataclass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class StandaloneIngredient(
    val id: String = Uuid.random().toString(),
    val name: String = "",
    val kcalPer100g: Int = 0,
    val tags: List<String> = listOf()
)
