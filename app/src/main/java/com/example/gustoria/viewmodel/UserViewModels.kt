package com.example.gustoria.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gustoria.dataclass.CookingRole
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.gustoria.SessionManager

//validation state
data class ProfileValidation(
    val nicknameError: String = "",
    val phoneError: String = "",
    val emailError: String = "",
    val cookingRoleError: String = "",
    val descriptionError: String = "",
    val isValid: Boolean = true
)

//placeholders data (poi da sostituire)
data class UserCollection(
    val title: String,
    val subtitle: String
)

data class UserActivity(
    val title: String,
    val subtitle: String
)

// Data class variables visible to all ViewModels (commentato per cambio logica)
/*private val loggedInUser = mutableStateOf(
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
)*/

class OwnedProfileViewModel(
    private val userRepo: UserRepoInterface
) : ViewModel() {

    // Stato dell'utente loggato preso dal repo (può essere null inizialm)
    val user: StateFlow<User?> = userRepo
        .getUserById(SessionManager.CURRENT_LOGGED_IN_USER_ID)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Bozza modificabile durante l'editing
    var editableUser by mutableStateOf<User?>(null)
        private set

    var validation by mutableStateOf(ProfileValidation())
        private set

    var isEditing by mutableStateOf(false)
        private set

    fun startEditing() { // click on edit
        editableUser = user.value
        validation = ProfileValidation()
        isEditing = true
    }
    fun cancelEditing() {
        editableUser = user.value
        validation = ProfileValidation()
        isEditing = false
    }

    fun validateAndSave() {
        val draft = editableUser ?: return

        var currentNicknameError = ""
        var currentPhoneError = ""
        var currentEmailError = ""
        var cookingRoleError = ""
        var descriptionError = ""

        if (draft.nickname.isBlank()) {
            currentNicknameError = "Nickname cannot be blank"
        }

        val phone = draft.phoneNumber.trim().replace(" ", "")
        if (phone.isBlank()) {
            currentPhoneError = "Phone number cannot be blank"
        } else if (phone.length !in 7..15) {
            currentPhoneError = "Phone number length is invalid"
        } else {
            val isValidPhone = phone.withIndex().all { (index, char) ->
                if (index == 0 && char == '+') true
                else char.isDigit()
            }
            if (!isValidPhone) {
                currentPhoneError = "Phone number must contain only digits"
            }
        }

        val email = draft.email.trim()
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

        if (draft.cookingRole == CookingRole.NONE) {
            cookingRoleError = "Please select a role"
        }

        val description = draft.description.trim()
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
            // Persistenza nel repo: aggiorna la fonte (?)
            viewModelScope.launch {
                userRepo.updateUser(draft.internalId, draft)
                isEditing = false
            }
        }
    }

    // Setters per la bozza
    fun setNickname(nickname: String) {
        editableUser = editableUser?.copy(nickname = nickname)
    }

    fun setDescription(description: String) {
        editableUser = editableUser?.copy(description = description)
    }

    fun setPhoneNumber(phone: String) {
        editableUser = editableUser?.copy(phoneNumber = phone)
    }

    fun setEmail(email: String) {
        editableUser = editableUser?.copy(email = email)
    }

    // cookingRole  riceve sempre un CookingRole !! (per cambio logica)
    fun setCookingRole(role: CookingRole) {
        editableUser = editableUser?.copy(cookingRole = role)
    }

    fun setCuisinePreferencesFromText(text: String) {
        editableUser = editableUser?.copy(cuisinePreferences = text.toTagList())
    }

    fun setDietaryRestrictionsFromText(text: String) {
        editableUser = editableUser?.copy(dietaryRestrictions = text.toTagList())
    }

    fun setFavoriteIngredientsFromText(text: String) {
        editableUser = editableUser?.copy(favoriteIngredients = text.toTagList())
    }

    fun setProfileImageUri(uri: String?) {
        editableUser = editableUser?.copy(profileImageUri = uri)
    }

    private fun String.toTagList(): List<String> =
        split(",").map { it.trim() }.filter { it.isNotBlank() }

    companion object {
        fun factory(userRepo: UserRepoInterface): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OwnedProfileViewModel(userRepo) as T
            }
    }
}


class OtherProfileViewModel(
    private val userRepo: UserRepoInterface,
    private val viewedUserId: String
) : ViewModel() {

    val user: StateFlow<User?> = userRepo
        .getUserById(viewedUserId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Per ora placeholder hardcoded, in seguito popolato da Review/Recipe repos
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

    var currentTab by mutableIntStateOf(0)
        private set

    fun toggleFollow() {
        isFollowing = !isFollowing // per ora non persiste sul repo
    }
    fun changeTab(index: Int) {
        currentTab = index
    }

    companion object {
        fun factory(
            userRepo: UserRepoInterface,
            viewedUserId: String
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OtherProfileViewModel(userRepo, viewedUserId) as T
            }
    }
}

/*
class RecipeViewModel : ViewModel() {
    //var recipe by viewRecipe

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
} */
