package edu.cit.tooltrack.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.tooltrack.api.ToolCategory
import edu.cit.tooltrack.api.ToolItem
import edu.cit.tooltrack.api.ToolSearchService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val toolSearchService = ToolSearchService.create()

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<ToolItem>>(emptyList())
        private set

    var categories by mutableStateOf<List<ToolCategory>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isCategoriesExpanded by mutableStateOf(false)
        private set

    private var searchJob: Job? = null

    init {
        categories = listOf(
            ToolCategory(1, "Power Tools", "Various power tools", ""),
            ToolCategory(2, "Hand Tools", "Manual hand tools", ""),
            ToolCategory(3, "Garden Tools", "Tools for gardening", ""),
            ToolCategory(4, "Electrical Tools", "Tools for electrical work", ""),
            ToolCategory(5, "Plumbing Tools", "Tools for plumbing tasks", ""),
            ToolCategory(6, "Painting Tools", "Tools for painting projects", ""),
            ToolCategory(7, "Automotive Tools", "Tools for vehicle maintenance", ""),
            ToolCategory(8, "Measuring Tools", "Tools for precise measurements", ""),
            ToolCategory(9, "Safety Equipment", "Personal protective equipment", "")
        )

    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        searchJob?.cancel()

        if (query.isEmpty()) {
            searchResults = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce search typing
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = toolSearchService.searchTools(query)
                if (response.isSuccessful) {
                    searchResults = response.body()?.items ?: emptyList()
                } else {
                    errorMessage = "Failed to search: ${response.message()}"
                    searchResults = emptyList()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun searchByCategory(category: ToolCategory) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = toolSearchService.searchTools("", category.name)
                if (response.isSuccessful) {
                    searchResults = response.body()?.items ?: emptyList()
                } else {
                    errorMessage = "Failed to load category items: ${response.message()}"
                    searchResults = emptyList()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = toolSearchService.getCategories()
                if (response.isSuccessful) {
                    categories = response.body()?.categories ?: emptyList()
                } else {
                    errorMessage = "Failed to load categories: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleCategoriesExpanded() {
        isCategoriesExpanded = !isCategoriesExpanded
    }
}
