package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.dataclass.RecentSearch
import com.example.gustoria.domain.UserRepoInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.gustoria.ui.recipe.RecipeFilters
import com.example.gustoria.data.auth.SessionManagerFacade
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi


class SearchViewModel(
    private val userRepository: UserRepoInterface
) : ViewModel() {

    private val currentUserId: String?
        get() = SessionManagerFacade.currentUserId.value

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentSearches: StateFlow<List<RecentSearch>> =
        SessionManagerFacade.currentUserId
            .flatMapLatest { uid ->
                if (uid.isNullOrBlank()) flowOf(emptyList())
                else userRepository.getRecentSearches(uid)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addRecentSearch(filters: RecipeFilters) {
        val uid = currentUserId ?: return
        val title = filters.generateDisplayTitle()
        if (title.isBlank() || title == "All Recipes") return
        viewModelScope.launch {
            userRepository.addRecentSearch(uid, title, filters)
        }
    }

    fun removeRecentSearch(id: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            userRepository.removeRecentSearch(uid, id)
        }
    }

    fun clearAllRecentSearches() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            userRepository.clearAllRecentSearches(uid)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GustoriaApplication)
                val userRepository = application.container.userRepository
                SearchViewModel(userRepository)
            }
        }
    }
}
