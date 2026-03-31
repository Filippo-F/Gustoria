package com.example.gustoria

data class User(val firstName : String) {
    val internalID : Int = 101
    val name : String

    init {
        name = firstName
    }
}
