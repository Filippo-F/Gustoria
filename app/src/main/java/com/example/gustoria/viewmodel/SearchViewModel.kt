package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RecentSearch(val id: String, val title: String, val subtitle: String)
data class TrendingCategory(val id: String, val title: String, val imageUrl: String?)

class SearchViewModel : ViewModel() {

    // Trending Searches
    val trendingSearches = listOf("#Pizza", "#Sushi")

    // Recent Searches
    private val _recentSearches = MutableStateFlow(
        listOf(
            RecentSearch("1", "Summer Harvest Buddha Bowl", "SEARCHED 2H AGO"),
            RecentSearch("2", "Smoked Paprika Roast Salmon", "SEARCHED YESTERDAY"),
            RecentSearch("3", "Artisanal Sourdough Pizza Base", "SEARCHED 3D AGO")
        )
    )
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    // Trending Categories
    val trendingCategories = listOf(
        TrendingCategory("c1", "Spaghetti", null),
        TrendingCategory("c2", "Pizza", null),
        TrendingCategory("c3", "Sushi", null),
        TrendingCategory("c4", "Salad", null),
        TrendingCategory("c5", "Soup", null),
        TrendingCategory("c6", "Risotto", null),
        TrendingCategory("c7", "Burger", null),
        TrendingCategory("c8", "Tiramisù", null) // meglio sweets?
    )

    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearAllRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun removeRecentSearch(id: String) {
        _recentSearches.update { list -> list.filter { it.id != id } }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel()
            }
        }
    }
}
