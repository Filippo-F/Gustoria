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

    val trendingSearches = listOf("#Pizza", "#Sushi")

    private val _recentSearches = MutableStateFlow(
        listOf(
            RecentSearch("1", "Summer Harvest Buddha Bowl", "SEARCHED 2H AGO"),
            RecentSearch("2", "Smoked Paprika Roast Salmon", "SEARCHED YESTERDAY"),
            RecentSearch("3", "Artisanal Sourdough Pizza Base", "SEARCHED 3D AGO")
        )
    )
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    val trendingCategories = listOf(
        TrendingCategory("c1", "Spaghetti", "https://images.unsplash.com/photo-1516100882582-96c3a05fe590?auto=format&fit=crop&w=300&q=80"),
        TrendingCategory("c2", "Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=300&q=80"),
        TrendingCategory("c3", "Sushi", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=300&q=80"),
        TrendingCategory("c4", "Salad", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=300&q=80"),
        TrendingCategory("c5", "Soup", "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=300&q=80"),
        TrendingCategory("c6", "Risotto", "https://images.unsplash.com/photo-1476124369491-e7addf5db371?auto=format&fit=crop&w=300&q=80"),
        TrendingCategory("c7", "Burger", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=300&q=80"),
        TrendingCategory("c8", "Dessert", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&w=300&q=80")
    )
    
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
