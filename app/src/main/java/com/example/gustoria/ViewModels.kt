package com.example.gustoria

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class OwnedProfileViewModel : ViewModel() {
    var user by mutableStateOf(UserClass(
        fullName = "Name Surname",
        nickname = "SuperChef",
        email = "chef@gustoria.it",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean eu volutpat massa. Nulla augue nunc, tempus eu risus at, fermentum varius urna. Aliquam sollicitudin mi accumsan, porttitor neque quis, euismod erat. Vestibulum id metus dui. Sed mauris orci, scelerisque ac maximus sed, pellentesque id nunc."
    ))
        private set

    var validation by mutableStateOf(true)
        private set

    var isEditing by mutableStateOf(false)
        private set

    fun edit() {
        isEditing = true
    }

    // Setters

    fun setFullName(fullName: String) {
        user = user.copy(fullName = fullName)
    }

    fun setNickname(nickname: String) {
        user = user.copy(nickname = nickname)
    }

    fun setEmail(email: String) {
        user = user.copy(email = email)
    }

    fun setDescription(description: String) {
        user = user.copy(description = description)
    }
}

class OtherProfileViewModel : ViewModel() {
    // ViewModel logic for Other User Profile
}

class RecipeViewModel : ViewModel() {
    // ViewModel logic for Recipe View
}
