package com.example.gustoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RecentSearch(val id: String, val title: String, val subtitle: String)
data class TrendingCategory(val id: String, val title: String, val imageUrl: String?)

class SearchViewModel : ViewModel() {

    // 1. Trending Searches
    val trendingSearches = listOf("#Sourdough", "#AutumnVibes", "#Fermentation", "#QuickBites")

    // 2. Recent Searches
    private val _recentSearches = MutableStateFlow(
        listOf(
            RecentSearch("1", "Summer Harvest Buddha Bowl", "SEARCHED 2H AGO"),
            RecentSearch("2", "Smoked Paprika Roast Salmon", "SEARCHED YESTERDAY"),
            RecentSearch("3", "Artisanal Sourdough Pizza Base", "SEARCHED 3D AGO")
        )
    )
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    // 3. Trending Categories
    val trendingCategories = listOf(
        TrendingCategory("c1", "Seasonal Harvest", null),
        TrendingCategory("c2", "Quick Weeknight Dinners", null)
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
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = SearchViewModel() as T
        }
    }
}
