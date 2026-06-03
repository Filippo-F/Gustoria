package com.example.gustoria.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.dataclass.CookingRole
import com.example.gustoria.dataclass.User
import com.example.gustoria.domain.RecipeRepoInterface
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.gustoria.data.auth.SessionManagerFacade

data class ProfileValidation(
    val nicknameError: String = "",
    val firstNameError: String = "",
    val lastNameError: String = "",
    val phoneError: String = "",
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

@OptIn(ExperimentalCoroutinesApi::class)
class OwnedProfileViewModel(
    private val userRepo: UserRepoInterface,
    private val recipeRepo: RecipeRepoInterface
) : ViewModel() {

    val user: StateFlow<User?> = SessionManagerFacade.currentUserId
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) flowOf(null)
            else userRepo.getUserById(uid)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val recipeCount: StateFlow<Int> = SessionManagerFacade.currentUserId
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) flowOf(emptyList())
            else recipeRepo.getRecipeByOwner(uid)
        }
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Somma totale dei like ricevuti su tutte le ricette dell'utente loggato
    val likeCount: StateFlow<Int> = SessionManagerFacade.currentUserId
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) flowOf(0)
            else recipeRepo.getLikesCountForOwner(uid)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

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
    var editingPhone by mutableStateOf(false)
        private set

    fun toggleEditingNickname() { editingNickname = !editingNickname }
    fun toggleEditingFirstName() { editingFirstName = !editingFirstName }
    fun toggleEditingLastName() { editingLastName = !editingLastName }
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
        editingPhone = false
    }

    fun validateAndSave() {
        val draft = editableUser ?: return

        var currentNicknameError = ""
        var currentFirstNameError = ""
        var currentLastNameError = ""
        var currentPhoneError = ""
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
                && currentPhoneError.isBlank()
                && cookingRoleError.isBlank()
                && descriptionError.isBlank()

        validation = ProfileValidation(
            nicknameError = currentNicknameError,
            firstNameError = currentFirstNameError,
            lastNameError = currentLastNameError,
            phoneError = currentPhoneError,
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
        editableUser = editableUser?.copy(firstName = firstName)
    }

    fun setLastName(lastName: String) {
        editableUser = editableUser?.copy(lastName = lastName)
    }

    fun setDescription(description: String) {
        editableUser = editableUser?.copy(description = description)
    }

    fun setPhoneNumber(phone: String) {
        editableUser = editableUser?.copy(phoneNumber = phone)
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
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val userRepository = application.container.userRepository
                val recipeRepository = application.container.recipeRepository
                OwnedProfileViewModel(userRepository, recipeRepository)
            }
        }
    }
}


class OtherProfileViewModel(
    private val userRepo: UserRepoInterface,
    private val recipeRepo: RecipeRepoInterface,
    private val viewedUserId: String
) : ViewModel() {

    val user: StateFlow<User?> = userRepo
        .getUserById(viewedUserId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val recipeCount: StateFlow<Int> = recipeRepo
        .getRecipeByOwner(viewedUserId)
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Somma totale dei like ricevuti su tutte le ricette del profilo visualizzato
    val likeCount: StateFlow<Int> = recipeRepo
        .getLikesCountForOwner(viewedUserId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

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
            viewedUserId: String
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val userRepository = application.container.userRepository
                val recipeRepository = application.container.recipeRepository
                OtherProfileViewModel(userRepository, recipeRepository, viewedUserId)
            }
        }
    }
}
