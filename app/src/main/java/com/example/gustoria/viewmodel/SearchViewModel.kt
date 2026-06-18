package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gustoria.GustoriaApplication
import com.example.gustoria.dataclass.RecentSearch
import com.example.gustoria.domain.UserRepoInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SearchViewModel(
    private val userRepository: UserRepoInterface
) : ViewModel() {

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    val recentSearches: StateFlow<List<RecentSearch>> = run {
        val uid = currentUserId
        if (uid != null) {
            userRepository.getRecentSearches(uid)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        } else {
            flowOf(emptyList<RecentSearch>()).stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
            )
        }
    }

    fun addRecentSearch(title: String) {
        val uid = currentUserId ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            userRepository.addRecentSearch(uid, title)
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
