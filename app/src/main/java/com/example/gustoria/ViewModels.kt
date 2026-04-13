package com.example.gustoria

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class OwnedProfileViewModel : ViewModel() {
    var user by mutableStateOf(
        UserClass(
            fullName = "Name Surname",
            nickname = "SuperChef",
            email = "chef@gustoria.it",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean eu volutpat massa.",
            phoneNumber = "+39 333 1234567",
            cookingRole = CookingRole.HOME_COOK,
            cuisinePreferences = listOf("Italian", "Japanese", "Mexican"),
            dietaryRestrictions = listOf("Gluten-Free"),
            favoriteIngredients = listOf("Garlic", "Olive Oil", "Basil"),
        )
    )
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
    fun setNickname(nickname: String) { user = user.copy(nickname = nickname) }
    fun setDescription(description: String) { user = user.copy(description = description) }
    fun setPhoneNumber(phone: String?) { user = user.copy(phoneNumber = phone) }
    fun setCookingRole(role: CookingRole) { user = user.copy(cookingRole = role) }
    fun setCuisinePreferences(list: List<String>) { user = user.copy(cuisinePreferences = list) }
    fun setDietaryRestrictions(list: List<String>) { user = user.copy(dietaryRestrictions = list) }
    fun setFavoriteIngredients(list: List<String>) { user = user.copy(favoriteIngredients = list) }
}

class OtherProfileViewModel : ViewModel() {
    // ViewModel logic for Other User Profile
}

class RecipeViewModel : ViewModel() {
    // ViewModel logic for Recipe View
}
