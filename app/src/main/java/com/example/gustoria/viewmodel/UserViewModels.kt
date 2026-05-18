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

data class ProfileValidation(
    val nicknameError: String = "",
    val firstNameError: String = "",
    val lastNameError: String = "",
    val passwordError: String = "",
    val phoneError: String = "",
    val emailError: String = "",
    val cookingRoleError: String = "",
    val descriptionError: String = "",
    val isValid: Boolean = true
)

//placeholders data
data class UserCollection(
    val title: String,
    val subtitle: String
)

data class UserActivity(
    val title: String,
    val subtitle: String
)

class OwnedProfileViewModel(
    private val userRepo: UserRepoInterface
) : ViewModel() {

    //state of logged user taken from repo (can be null initially)
    val user: StateFlow<User?> = userRepo
        .getUserById(SessionManager.CURRENT_LOGGED_IN_USER_ID)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // bozza modifiable durante l'editing
    var editableUser by mutableStateOf<User?>(null)
        private set

    var validation by mutableStateOf(ProfileValidation())
        private set

    var isEditing by mutableStateOf(false)
        private set

    var cuisinePreferencesText by mutableStateOf("")
        private set
    var dietaryRestrictionsText by mutableStateOf("")
        private set
    var favoriteIngredientsText by mutableStateOf("")
        private set

    // State for individual field editing in ProfileInfoScreen
    var editingNickname by mutableStateOf(false)
        private set
    var editingFirstName by mutableStateOf(false)
        private set
    var editingLastName by mutableStateOf(false)
        private set
    var editingEmail by mutableStateOf(false)
        private set
    var editingPassword by mutableStateOf(false)
        private set
    var editingPhone by mutableStateOf(false)
        private set

    fun toggleEditingNickname() { editingNickname = !editingNickname }
    fun toggleEditingFirstName() { editingFirstName = !editingFirstName }
    fun toggleEditingLastName() { editingLastName = !editingLastName }
    fun toggleEditingEmail() { editingEmail = !editingEmail }
    fun toggleEditingPassword() { editingPassword = !editingPassword }
    fun toggleEditingPhone() { editingPhone = !editingPhone }

    fun startEditing() { // click on edit
        editableUser = user.value
        validation = ProfileValidation()
        isEditing = true
        resetFieldEditingStates()
        cuisinePreferencesText = editableUser?.cuisinePreferences?.joinToString(", ") ?: ""
        dietaryRestrictionsText = editableUser?.dietaryRestrictions?.joinToString(", ") ?: ""
        favoriteIngredientsText = editableUser?.favoriteIngredients?.joinToString(", ") ?: ""
    }
    fun cancelEditing() {
        editableUser = user.value
        validation = ProfileValidation()
        isEditing = false
        resetFieldEditingStates()
        cuisinePreferencesText = editableUser?.cuisinePreferences?.joinToString(", ") ?: ""
        dietaryRestrictionsText = editableUser?.dietaryRestrictions?.joinToString(", ") ?: ""
        favoriteIngredientsText = editableUser?.favoriteIngredients?.joinToString(", ") ?: ""
    }

    private fun resetFieldEditingStates() {
        editingNickname = false
        editingFirstName = false
        editingLastName = false
        editingEmail = false
        editingPassword = false
        editingPhone = false
    }

    fun validateAndSave() {
        val draft = editableUser ?: return

        var currentNicknameError = ""
        var currentFirstNameError = ""
        var currentLastNameError = ""
        var currentPasswordError = ""
        var currentPhoneError = ""
        var currentEmailError = ""
        var cookingRoleError = ""
        var descriptionError = ""

        if (draft.nickname.isBlank()) {
            currentNicknameError = "Nickname cannot be blank"
        }

        if (draft.firstName.isBlank()) {
            currentFirstNameError = "First name cannot be blank"
        }

        if (draft.lastName.isBlank()) {
            currentLastNameError = "Last name cannot be blank"
        }

        if (draft.password.isBlank()) {
            currentPasswordError = "Password cannot be blank"
        } else if (draft.password.length < 6) {
            currentPasswordError = "Password must be at least 6 characters"
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
                && currentFirstNameError.isBlank()
                && currentLastNameError.isBlank()
                && currentPasswordError.isBlank()
                && currentPhoneError.isBlank()
                && currentEmailError.isBlank()
                && cookingRoleError.isBlank()
                && descriptionError.isBlank()

        validation = ProfileValidation(
            nicknameError = currentNicknameError,
            firstNameError = currentFirstNameError,
            lastNameError = currentLastNameError,
            passwordError = currentPasswordError,
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

    fun setFirstName(firstName: String) {
        editableUser = editableUser?.let { 
            it.copy(
                firstName = firstName,
                fullName = "$firstName ${it.lastName}".trim()
            )
        }
    }

    fun setLastName(lastName: String) {
        editableUser = editableUser?.let {
            it.copy(
                lastName = lastName,
                fullName = "${it.firstName} $lastName".trim()
            )
        }
    }

    fun setPassword(password: String) {
        editableUser = editableUser?.copy(password = password)
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
        cuisinePreferencesText = text
        editableUser = editableUser?.copy(cuisinePreferences = text.toTagList())
    }

    fun setDietaryRestrictionsFromText(text: String) {
        dietaryRestrictionsText = text
        editableUser = editableUser?.copy(dietaryRestrictions = text.toTagList())
    }

    fun setFavoriteIngredientsFromText(text: String) {
        favoriteIngredientsText = text
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
