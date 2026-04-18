package com.example.gustoria

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class ProfileValidation(
    val nicknameError: String = "",
    val phoneError: String = "",
    val emailError: String = "",
    val cookingRoleError: String = "",
    val descriptionError: String = "",
    val isValid: Boolean = true
)

// Shared user state visible to all ViewModels in this file
private val loggedInUser = mutableStateOf(
    UserClass(
        fullName = "Mario Rossi",
        nickname = "SuperChef",
        email = "chef@gustoria.it",
        description = "Simple ingredients, great passion, amazing food.",
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

    var editableUser by mutableStateOf(user) // draft while modifying
        private set

    var validation by mutableStateOf(ProfileValidation())
        private set

    var isEditing by mutableStateOf(false)
        private set

    fun startEditing() { // Click on edit
        editableUser = user
        validation = ProfileValidation()
        isEditing = true
    }

    fun cancelEditing() { // Delete changes
        editableUser = user
        validation = ProfileValidation()
        isEditing = false
    }

    fun validateAndSave() {
        var currentNicknameError = ""
        var currentPhoneError = ""
        var currentEmailError = ""
        var cookingRoleError = ""
        var descriptionError = ""

        if (editableUser.nickname.isBlank()) {
            currentNicknameError = "Nickname cannot be blank"
        }

        val phone = editableUser.phoneNumber.orEmpty()
        if (phone.isBlank() || (phone.isNotBlank() && phone.length < 6)) {
            currentPhoneError = "Invalid phone number"
        }

        if (editableUser.email.isBlank()) {
            currentEmailError = "Email cannot be blank"
        } else if (!editableUser.email.contains("@")) {
            currentEmailError = "Email must contain @"
        }

        if (editableUser.cookingRole == null) {
            cookingRoleError = "Please select a role"
        }

        if (editableUser.description.length > 150) {
            descriptionError = "Too long"
        }

        val formIsValid = currentNicknameError.isBlank()
                && currentPhoneError.isBlank()
                && currentEmailError.isBlank()
                && cookingRoleError.isBlank()
                && descriptionError.isBlank()

        validation = ProfileValidation(
            nicknameError = currentNicknameError,
            phoneError = currentPhoneError,
            emailError = currentEmailError,
            cookingRoleError = cookingRoleError,
            descriptionError = descriptionError,
            isValid = formIsValid
        )

        if (formIsValid) {
            user = editableUser
            isEditing = false
        }
    }

    // Setters per editableUser

    fun setNickname(nickname: String) { editableUser = editableUser.copy(nickname = nickname) }
    fun setDescription(description: String) { editableUser = editableUser.copy(description = description) }
    fun setPhoneNumber(phone: String) { editableUser = editableUser.copy(phoneNumber = phone) }

    fun setEmail(email: String) { editableUser = editableUser.copy(email = email) }
    fun setCookingRole(role: CookingRole?) { editableUser = editableUser.copy(cookingRole = role) }
    fun setCuisinePreferencesFromText(text: String) {
        editableUser = editableUser.copy(
            cuisinePreferences = text.toTagList()
        )
    }

    fun setDietaryRestrictionsFromText(text: String) {
        editableUser = editableUser.copy(
            dietaryRestrictions = text.toTagList()
        )
    }

    fun setFavoriteIngredientsFromText(text: String) {
        editableUser = editableUser.copy(
            favoriteIngredients = text.toTagList()
        )
    }

    private fun String.toTagList(): List<String> {
        return split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    // Setters per editableUser
    fun setProfileImageUri(uri: String?) { editableUser = editableUser.copy(profileImageUri = uri) }

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

    var currentTab by mutableStateOf(0)
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

    fun changeTab (index: Int) {
        currentTab = index
    }
}

class RecipeViewModel : ViewModel() {
    val recipe = RecipeProposal(
        title = "Pasta al Pomodoro",
        image = R.drawable.guest_user_profile_pic, //placeholder per ora
        cost = "€",
        difficulty = "Easy",
        cookingTimeMinutes = 20,
        servings = 2,
        ingredients = listOf(
            Ingredient("Spaghetti", "200g"),
            Ingredient("Tomatoes", "300g"),
            Ingredient("Garlic", "2 cloves"),
            Ingredient("Oil EVO", "3 Spoons")
        ),
        steps = listOf(
            "Bring salted water to a boil.",
            "Sauté the garlic in oil for 2 minutes.",
            "Add the tomatoes and cook for 10 minutes.",
            "Drain the pasta al dente and toss with the sauce."
        )
    )
}
