package com.example.gustoria.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
import com.example.gustoria.data.utils.ImageUploader
import com.example.gustoria.dataclass.Notification
import com.example.gustoria.dataclass.NotificationType
import com.example.gustoria.domain.NotificationRepoInterface
import com.example.gustoria.dataclass.Recipe
import kotlinx.coroutines.flow.first

data class ProfileValidation(
    val nicknameError: String = "",
    val firstNameError: String = "",
    val lastNameError: String = "",
    val phoneError: String = "",
    val cookingRoleError: String = "",
    val descriptionError: String = "",
    val isValid: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class OwnedProfileViewModel(
    private val userRepo: UserRepoInterface,
    private val recipeRepo: RecipeRepoInterface,
    private val notificationRepo: NotificationRepoInterface
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

    // Total likes received on all recipes
    val likeCount: StateFlow<Int> = SessionManagerFacade.currentUserId
        .flatMapLatest { uid ->
            if (uid.isNullOrBlank()) flowOf(0)
            else recipeRepo.getLikesCountForOwner(uid)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Editable draft
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

    var isSubmitting by mutableStateOf(false)
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

    fun startEditing() {
        editableUser = user.value
        validation = ProfileValidation()
        isEditing = true
        resetFieldEditingStates()
        cuisinePreferencesText = editableUser?.cuisinePreferences?.joinToString(", ") ?: ""
        dietaryRestrictionsText = editableUser?.dietaryRestrictions?.joinToString(", ") ?: ""

    }
    fun cancelEditing() {
        editableUser = user.value
        validation = ProfileValidation()
        isEditing = false
        resetFieldEditingStates()
        cuisinePreferencesText = editableUser?.cuisinePreferences?.joinToString(", ") ?: ""
        dietaryRestrictionsText = editableUser?.dietaryRestrictions?.joinToString(", ") ?: ""

    }

    private fun resetFieldEditingStates() {
        editingNickname = false
        editingFirstName = false
        editingLastName = false
        editingPhone = false
    }

    fun validateAndSave(onSuccess: () -> Unit) {
        val draft = editableUser ?: return
        if (isSubmitting) return

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
            isEditing = false
            isSubmitting = true

            viewModelScope.launch {
                try {
                    val publicProfileUrl = if (draft.profileImageUri != null && !draft.profileImageUri.startsWith("http")) {
                        // Delete old image if a new one is uploaded
                        user.value?.profileImageUri?.let { oldUrl ->
                            ImageUploader.deleteImage(oldUrl, "profiles")
                        }
                        ImageUploader.uploadImage(draft.profileImageUri, "profiles")
                    } else if (draft.profileImageUri == null) {
                        // Delete image if it was removed
                        user.value?.profileImageUri?.let { oldUrl ->
                            ImageUploader.deleteImage(oldUrl, "profiles")
                        }
                        null
                    } else {
                        draft.profileImageUri
                    }

                    val finalDraft = draft.copy(profileImageUri = publicProfileUrl)
                    userRepo.updateUser(finalDraft.internalId, finalDraft)

                    // Preferences changed: clear stale recommended notifications
                    // so they get regenerated fresh on next Notifications screen open
                    notificationRepo.deleteRecommendedNotificationsForUser(finalDraft.internalId)

                    // Upload complete, so close screen
                    onSuccess()
                } finally {
                    isSubmitting = false
                }
            }
        }
    }
    // Draft setters
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

    // Update cooking role
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

    fun setProfileImageUri(uri: String?) {
        editableUser = editableUser?.copy(profileImageUri = uri)
    }

    fun toggleCuisinePreference(cuisine: String) {
        val current = editableUser?.cuisinePreferences ?: emptyList()
        val next = if (current.contains(cuisine)) {
            current.filter { it != cuisine }
        } else {
            current + cuisine
        }
        editableUser = editableUser?.copy(cuisinePreferences = next)
        cuisinePreferencesText = next.joinToString(", ")
    }

    fun toggleDietaryRestriction(diet: String) {
        val current = editableUser?.dietaryRestrictions ?: emptyList()
        val next = if (current.contains(diet)) {
            current.filter { it != diet }
        } else {
            current + diet
        }
        editableUser = editableUser?.copy(dietaryRestrictions = next)
        dietaryRestrictionsText = next.joinToString(", ")
    }
    fun toggleMealType(meal: String) {
        val current = editableUser?.favouriteMealTypes ?: emptyList()
        val next = if (current.contains(meal)) current - meal else current + meal
        editableUser = editableUser?.copy(favouriteMealTypes = next)
    }

    private fun String.toTagList(): List<String> =
        split(",").map { it.trim() }.filter { it.isNotBlank() }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val userRepository = application.container.userRepository
                val recipeRepository = application.container.recipeRepository
                val notificationRepository = application.container.notificationRepository
                OwnedProfileViewModel(userRepository, recipeRepository, notificationRepository)
            }
        }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
class OtherProfileViewModel(
    private val userRepo: UserRepoInterface,
    private val recipeRepo: RecipeRepoInterface,
    private val notificationRepo: NotificationRepoInterface,
    private val viewedUserId: String
) : ViewModel() {

    private val currentUserId: String
        get() = SessionManagerFacade.currentUserId.value ?: ""

    val user: StateFlow<User?> = userRepo
        .getUserById(viewedUserId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val recipeCount: StateFlow<Int> = recipeRepo
        .getRecipeByOwner(viewedUserId)
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val likeCount: StateFlow<Int> = recipeRepo
        .getLikesCountForOwner(viewedUserId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val isFollowing: StateFlow<Boolean> = SessionManagerFacade.currentUserId
        .flatMapLatest { currentUid ->
            if (currentUid != null && currentUid.isNotBlank()) {
                userRepo.isFollowing(currentUid, viewedUserId)
            } else {
                flowOf(false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val recipes: StateFlow<List<Recipe>> = recipeRepo
        .getRecipeByOwner(viewedUserId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun toggleFollow() {
        viewModelScope.launch {
            if (isFollowing.value) {
                userRepo.unfollowUser(currentUserId, viewedUserId)
            } else {
                userRepo.followUser(currentUserId, viewedUserId)
                val recipient = userRepo.getUserById(viewedUserId).first()
                if (recipient?.pushNotificationsEnabled == true) {
                    notificationRepo.addNotification(
                        Notification(
                            recipientUserId = viewedUserId,
                            type = NotificationType.NEW_FOLLOWER.name,
                            title = "You have a new follower!",
                            message = "Someone started following you.",
                            targetRecipeId = null,
                            targetUserId = currentUserId
                        )
                    )
                }
            }
        }
    }

    companion object {
        fun factory(viewedUserId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                OtherProfileViewModel(
                    userRepo = application.container.userRepository,
                    recipeRepo = application.container.recipeRepository,
                    notificationRepo = application.container.notificationRepository,
                    viewedUserId = viewedUserId
                )
            }
        }
    }
}
