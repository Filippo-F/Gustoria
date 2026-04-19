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

// Data class variables visible to all ViewModels
private val loggedInUser = mutableStateOf(
    UserClass(
        fullName = "Mario Rossi",
        nickname = "SuperChef",
        email = "chef@gustoria.it",
        description = "Simple ingredients, great passion, amazing food.",
        phoneNumber = "+39 333 1234567",
        cookingRole = CookingRole.HOME_COOK,

        cuisinePreferences = listOf("Italian", "Japanese"),
        dietaryRestrictions = listOf("Vegan"),
        favoriteIngredients = listOf("Garlic", "Nuts"),

        numberOfRecipes = 42,
        numberOfFollowers = 1200,
        numberOfLikes = 850
    )
)

private val viewRecipe = mutableStateOf(
    RecipeProposal(
        title = "Napoletana's Spaghetti",
        cost = "€",
        difficulty = "Easy",
        cookingTimeMinutes = 20,
        servings = 2,
        rating = 4.5f,
        reviews = 100,
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
        ),
        description = "Pasta al pomodoro is an iconic Italian dish consisting of pasta—traditionally spaghetti—tossed in a simple, fresh tomato sauce, olive oil, garlic, and basil."
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

        val phone = editableUser.phoneNumber.orEmpty().trim().replace(" ", "")
        if (phone.isBlank()) {
            currentPhoneError = "Phone number cannot be blank"
        } else if (phone.length < 7 || phone.length > 15) {
            currentPhoneError = "Phone number length is invalid"
        } else {
            val isValid = phone.withIndex().all { (index, char) ->
                if (index == 0 && char == '+') true
                else char.isDigit()
            }

            if (!isValid) {
                currentPhoneError = "Phone number must contain only digits"
            }
        }

        val email = editableUser.email.trim()
        if (email.isBlank()) {
            currentEmailError = "Email cannot be blank"
        } else if (!email.contains("@")) {
            currentEmailError = "Email must contain @"
        } else if (email.count { it == '@' } != 1) {
            currentEmailError = "Email must contain only one @"
        } else {
            val parts = email.split("@")
            val localPart = parts[0]
            val domainPart = parts[1]

            if (localPart.isBlank()) {
                currentEmailError = "Invalid email format"
            } else if (!domainPart.contains(".")) {
                currentEmailError = "Domain must contain a dot"
            } else if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
                currentEmailError = "Invalid domain format"
            }
        }

        if (editableUser.cookingRole == null) {
            cookingRoleError = "Please select a role"
        }

        val description = editableUser.description.orEmpty().trim()
        if (description.length > 150) {
            descriptionError = "Maximum 150 characters"
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
    var recipe by viewRecipe

    var isFavorite by mutableStateOf(false)
        private set

    var isMade by mutableStateOf(false)
        private set

    fun toggleFavorite() {
        isFavorite = !isFavorite
    }

    fun toggleMade() {
        isMade = !isMade
    }
}
