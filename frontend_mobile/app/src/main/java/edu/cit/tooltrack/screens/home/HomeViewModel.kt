package edu.cit.tooltrack.screens.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.cit.tooltrack.api.ApiToolItem
import edu.cit.tooltrack.api.ToolCategory
import edu.cit.tooltrack.api.ToolItem
import edu.cit.tooltrack.api.ToolSearchService
import edu.cit.tooltrack.utils.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val toolSearchService = ToolSearchService.create()
    private val sessionManager = SessionManager(application.applicationContext)

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

    var searchResultTool by mutableStateOf<ApiToolItem?>(null)
        private set

    var selectedCategory by mutableStateOf<ToolCategory?>(null)
        private set

    // Original list of tools for the selected category (unfiltered)
    private var _allCategoryTools: List<ToolItem> = emptyList()

    // Store all tools fetched from the API for local search
    private var _allTools: List<ToolItem> = emptyList()

    var categoryTools by mutableStateOf<List<ToolItem>>(emptyList())
        private set

    // Search query for category filtering
    var categorySearchQuery by mutableStateOf("")
        private set

    var isCategoryLoading by mutableStateOf(false)
        private set

    var categoryErrorMessage by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null

    init {
        categories = listOf(
            ToolCategory(1, "Power", "Various power tools", ""),
            ToolCategory(2, "Hand", "Manual hand tools", ""),
            ToolCategory(3, "Garden", "Tools for gardening", ""),
            ToolCategory(4, "Electrical", "Tools for electrical work", ""),
            ToolCategory(5, "Plumbing", "Tools for plumbing tasks", ""),
            ToolCategory(6, "Painting", "Tools for painting projects", ""),
            ToolCategory(7, "Automotive", "Tools for vehicle maintenance", ""),
            ToolCategory(8, "Measuring", "Tools for precise measurements", ""),
            ToolCategory(9, "Safety", "Personal protective equipment", "")
        )

        // Load popular tools when the ViewModel is created
        loadPopularTools()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        searchJob?.cancel()

        if (query.isEmpty()) {
            // If query is empty, load popular tools instead of showing empty results
            loadPopularTools()
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
                // If _allTools is empty, try to load tools first
                if (_allTools.isEmpty()) {
                    loadPopularTools()
                    // Wait for loading to complete
                    delay(500)
                    // If still empty after loading, show error
                    if (_allTools.isEmpty()) {
                        errorMessage = "No tools available. Please try again later."
                        searchResults = emptyList()
                        isLoading = false
                        return@launch
                    }
                }

                // Filter tools locally based on the query
                val filteredTools = _allTools.filter { tool ->
                    tool.name.contains(query, ignoreCase = true) ||
                    tool.description.contains(query, ignoreCase = true) ||
                    tool.categoryName.contains(query, ignoreCase = true)
                }

                if (filteredTools.isNotEmpty()) {
                    searchResults = filteredTools
                    Log.d("HomeViewModel", "Found ${filteredTools.size} tools matching '$query'")
                } else {
                    errorMessage = "No tools found matching '$query'"
                    searchResults = emptyList()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                searchResults = emptyList()
                Log.e("HomeViewModel", "Error during search", e)
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
                // If _allTools is empty, try to load tools first
                if (_allTools.isEmpty()) {
                    loadPopularTools()
                    // Wait for loading to complete
                    delay(500)
                    // If still empty after loading, show error
                    if (_allTools.isEmpty()) {
                        errorMessage = "No tools available. Please try again later."
                        searchResults = emptyList()
                        isLoading = false
                        return@launch
                    }
                }

                // Filter tools locally based on the category
                val filteredTools = _allTools.filter { tool ->
                    tool.categoryName.equals(category.name, ignoreCase = true)
                }

                if (filteredTools.isNotEmpty()) {
                    searchResults = filteredTools
                    Log.d("HomeViewModel", "Found ${filteredTools.size} tools in category '${category.name}'")
                } else {
                    errorMessage = "No tools found in category '${category.name}'"
                    searchResults = emptyList()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                searchResults = emptyList()
                Log.e("HomeViewModel", "Error during category search", e)
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

    fun searchToolByName(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            searchResultTool = null

            try {
                // If _allTools is empty, try to load tools first
                if (_allTools.isEmpty()) {
                    loadPopularTools()
                    // Wait for loading to complete
                    delay(500)
                    // If still empty after loading, show error
                    if (_allTools.isEmpty()) {
                        errorMessage = "No tools available. Please try again later."
                        isLoading = false
                        return@launch
                    }
                }

                // First try to find an exact match in the local data
                val exactMatch = _allTools.firstOrNull { 
                    it.name.equals(query, ignoreCase = true) 
                }

                if (exactMatch != null) {
                    // Convert ToolItem to ApiToolItem for compatibility
                    val apiToolItem = ApiToolItem(
                        tool_id = exactMatch.id,
                        toolTransaction = emptyList(),
                        tool_condition = "UNKNOWN", // Not available in ToolItem
                        status = exactMatch.status.uppercase(),
                        category = exactMatch.categoryName,
                        name = exactMatch.name,
                        qr_code = null, // Not available in ToolItem
                        qr_code_name = null, // Not available in ToolItem
                        location = "Unknown", // Not available in ToolItem
                        description = exactMatch.description,
                        date_acquired = "", // Not available in ToolItem
                        image_url = exactMatch.imageUrl,
                        image_name = null, // Not available in ToolItem
                        created_at = "", // Not available in ToolItem
                        updated_at = null // Not available in ToolItem
                    )
                    searchResultTool = apiToolItem
                    Log.d("HomeViewModel", "Found exact match for '$query': ${exactMatch.name}")
                } else {
                    // If no exact match, try API call as fallback
                    try {
                        val sessionManager = SessionManager(getApplication<Application>().applicationContext)
                        val token = sessionManager.fetchAuthToken()
                        if (token.isNullOrEmpty()) {
                            errorMessage = "Session expired. Please log in again."
                            isLoading = false
                            return@launch
                        }
                        val response = ToolSearchService.create().searchToolByName(
                            token = "Bearer $token",
                            name = query
                        )
                        if (response.isSuccessful) {
                            searchResultTool = response.body()?.toolItem
                            Log.d("HomeViewModel", "Found tool via API: ${searchResultTool?.name}")
                        } else {
                            errorMessage = "No tool found matching '$query'"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.localizedMessage}"
                        Log.e("HomeViewModel", "Error searching for tool by name", e)
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
                Log.e("HomeViewModel", "Error in searchToolByName", e)
            } finally {
                isLoading = false
            }
        }
    }
    fun refreshPopularTools() {
        loadPopularTools()
    }

    fun clearSearchResult() {
        searchResultTool = null
        errorMessage = null

        // If _allTools is not empty, use it directly instead of reloading
        if (_allTools.isNotEmpty()) {
            searchResults = _allTools
            isLoading = false
        } else {
            // Otherwise, reload from API
            loadPopularTools()
        }
    }

    // Function to load popular tools from the API
    private fun loadPopularTools() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Get the token from SessionManager
                val token = sessionManager.fetchAuthToken()

                if (token == null) {
                    errorMessage = "You need to log in to view tools"
                    searchResults = emptyList()
                    isLoading = false
                    return@launch
                }

                // Format the token with "Bearer " prefix
                val authHeader = "Bearer $token"

                // Call the API
                Log.d("HomeViewModel", "Loading popular tools")
                val response = toolSearchService.getAllTools(authHeader)

                if (response.isSuccessful) {
                    val apiTools = response.body() ?: emptyList()
                    Log.d("HomeViewModel", "API returned ${apiTools.size} tools")

                    // Sort tools alphabetically by name and by availability
                    val sortedTools = apiTools.sortedWith(
                        compareBy<ApiToolItem> { 
                            // First sort by availability (AVAILABLE first)
                            if (it.status == "AVAILABLE") 0 else 1 
                        }.thenBy { 
                            // Then sort alphabetically by name
                            it.name 
                        }
                    )

                    // For each tool, log its measurements to identify any unusually large images
                    sortedTools.forEach { apiTool ->
                        Log.d("HomeViewModel", "Tool: ${apiTool.name}, " +
                                "image_url: ${apiTool.image_url ?: "null"}, " +
                                "status: ${apiTool.status}")
                    }

                    // Convert ApiToolItem to ToolItem for display
                    _allTools = sortedTools.map { apiTool ->
                        ToolItem(
                            id = apiTool.tool_id,
                            name = apiTool.name,
                            description = apiTool.description,
                            imageUrl = apiTool.image_url ?: "",
                            status = apiTool.status.lowercase(),
                            categoryId = 0, // Not available in API response
                            categoryName = apiTool.category ?: "Uncategorized"
                        )
                    }

                    // Set search results to all tools initially
                    searchResults = _allTools

                    Log.d("HomeViewModel", "Processed ${_allTools.size} tools for display")
                } else {
                    errorMessage = "Failed to load tools: ${response.message()}"
                    searchResults = emptyList()
                    Log.e("HomeViewModel", "API error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                searchResults = emptyList()
                Log.e("HomeViewModel", "Exception loading tools", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun selectCategory(category: ToolCategory) {
        selectedCategory = category
        loadCategoryTools(category, forceRefresh = true)
    }

    fun clearCategorySelection() {
        selectedCategory = null
        _allCategoryTools = emptyList()
        categoryTools = emptyList()
        categorySearchQuery = ""
        categoryErrorMessage = null
    }

    fun loadCategoryTools(category: ToolCategory, forceRefresh: Boolean = false) {
        if (_allCategoryTools.isNotEmpty() && !forceRefresh) return // Use cached
        viewModelScope.launch {
            isCategoryLoading = true
            categoryErrorMessage = null
            try {
                val token = sessionManager.fetchAuthToken()
                if (token.isNullOrEmpty()) {
                    categoryErrorMessage = "Session expired. Please log in again."
                    isCategoryLoading = false
                    return@launch
                }
                val endpointCategory = when (category.name.lowercase()) {
                    "power" -> "power+tools"
                    "hand" -> "hand+tools"
                    "garden" -> "garden+tools"
                    "electrical" -> "electrical+tools"
                    "plumbing" -> "plumbing+tools"
                    "painting" -> "painting"
                    "automotive" -> "automotive+tools"
                    "measuring" -> "measuring+tools"
                    "safety" -> "safety+equipment"
                    else -> category.name.replace(" ", "+").lowercase()
                }
                val response = toolSearchService.searchToolsByCategory(
                    token = "Bearer $token",
                    category = endpointCategory
                )
                if (response.isSuccessful) {
                    // Extract toolItem array from the response wrapper
                    val apiTools = response.body()?.toolItem ?: emptyList()
                    // Store all tools in the private property
                    _allCategoryTools = apiTools.map {
                        ToolItem(
                            id = it.tool_id,
                            name = it.name,
                            description = it.description,
                            imageUrl = it.image_url ?: "",
                            status = it.status.lowercase(),
                            categoryId = 0,
                            categoryName = it.category ?: ""
                        )
                    }
                    // Reset search query when loading new category
                    categorySearchQuery = ""
                    // Initialize filtered tools with all tools
                    categoryTools = _allCategoryTools
                } else {
                    categoryErrorMessage = "No tools found for this category."
                    _allCategoryTools = emptyList()
                    categoryTools = emptyList()
                }
            } catch (e: Exception) {
                categoryErrorMessage = "Error: ${e.localizedMessage}"
                _allCategoryTools = emptyList()
                categoryTools = emptyList()
            }
            isCategoryLoading = false
        }
    }

    // Update the category search query and filter results
    fun updateCategorySearchQuery(query: String) {
        categorySearchQuery = query
        filterCategoryTools()
    }

    // Filter category tools based on search query
    private fun filterCategoryTools() {
        categoryTools = if (categorySearchQuery.isBlank()) {
            _allCategoryTools
        } else {
            _allCategoryTools.filter { tool ->
                tool.name.contains(categorySearchQuery, ignoreCase = true)
            }
        }
    }

    fun toggleCategoriesExpanded() {
        isCategoriesExpanded = !isCategoriesExpanded
    }

    // Factory to create the ViewModel with application context
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
