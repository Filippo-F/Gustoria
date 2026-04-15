package com.example.gustoria

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class ProfileValidation(
    // TODO: inserire errori campi editabili
    val nicknameError: String = "",
    val phoneError: String = "",
    val isValid: Boolean = true
)



// Shared user state visible to all ViewModels in this file
private val loggedInUser = mutableStateOf(
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

        numberOfRecipes = 42,
        numberOfFollowers = 1200,
        numberOfLikes = 850
    )
)

class OwnedProfileViewModel : ViewModel() {
    var user by loggedInUser
        private set

    var editableUser by mutableStateOf(user) // bozza mentre sto modificando
        private set

    var validation by mutableStateOf(ProfileValidation())
        private set

    var isEditing by mutableStateOf(false)
        private set

    fun startEditing() { // quando premo EDIT
        editableUser = user
        validation = ProfileValidation()
        isEditing = true
    }

    fun cancelEditing() { // scarto modifiche
        editableUser = user
        validation = ProfileValidation()
        isEditing = false
    }

    fun validateAndSave() {
        var currentNicknameError = ""
        var currentPhoneError = ""

        if (editableUser.nickname.isBlank()) {
            currentNicknameError = "Nickname cannot be blank"
        }

        val phone = editableUser.phoneNumber.orEmpty()
        if (phone.isBlank() || (phone.isNotBlank() && phone.length < 6)) {
            currentPhoneError = "Invalid phone number"
        }

        val formIsValid = currentNicknameError.isBlank() && currentPhoneError.isBlank()

        validation = ProfileValidation(
            nicknameError = currentNicknameError,
            phoneError = currentPhoneError,
            isValid = formIsValid
        )

        if (formIsValid) {
            user = editableUser
            isEditing = false
        }
    }

    // Setters per editableUser

//    fun setFullName(fullName: String) {
//        editableUser = editableUser.copy(fullName = fullName)
//    }
    fun setNickname(nickname: String) { editableUser = editableUser.copy(nickname = nickname) }
    fun setDescription(description: String) { editableUser = editableUser.copy(description = description) }
    fun setPhoneNumber(phone: String) { editableUser = editableUser.copy(phoneNumber = phone) }
    fun setCookingRole(role: CookingRole?) { editableUser = editableUser.copy(cookingRole = role) }
    fun setCuisinePreferences(list: List<String>) { editableUser = editableUser.copy(cuisinePreferences = list) }
    fun setDietaryRestrictions(list: List<String>) { editableUser = editableUser.copy(dietaryRestrictions = list) }
    fun setFavoriteIngredients(list: List<String>) { editableUser = editableUser.copy(favoriteIngredients = list) }
}

class OtherProfileViewModel : ViewModel() {
    var user by loggedInUser
        private set

    val collections = listOf(
        UserCollection("Summer Harvest", "12 Recipes • 2.4k Views"),
        UserCollection("Artisan Bakes", "8 Recipes • 1.1k Views")
    )

    val recentActivities = listOf(
        UserActivity("Published \"Golden Turmeric Latte\"", "2 hours ago"),
        UserActivity("Liked Marco's \"Focaccia Masterclass\"", "Yesterday")
    )

    var isFollowing by mutableStateOf(false)
        private set

    fun toggleFollow() {
        isFollowing = !isFollowing
        if (isFollowing) {
            follow()
        } else {
            unfollow()
        }
    }

    fun follow() {
        user = user.copy(numberOfFollowers = user.numberOfFollowers + 1)
    }

    fun unfollow() {
        user = user.copy(numberOfFollowers = user.numberOfFollowers - 1)
    }
}

class RecipeViewModel : ViewModel() {
    // ViewModel logic for Recipe View
}
