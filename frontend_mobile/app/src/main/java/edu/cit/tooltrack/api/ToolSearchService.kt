package edu.cit.tooltrack.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Original ToolItem model for backward compatibility
data class ToolItem(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val status: String,
    val categoryId: Int,
    val categoryName: String
)

// New model that matches the API response from getAllTool endpoint
data class ApiToolItem(
    val tool_id: Int,
    val toolTransaction: List<Any>,
    val tool_condition: String,
    val status: String,
    val category: String?,
    val name: String,
    val qr_code: String?,
    val qr_code_name: String?,
    val location: String,
    val description: String,
    val date_acquired: String,
    val image_url: String?,
    val image_name: String?,
    val created_at: String,
    val updated_at: String?
)

// Wrapper class for category search response
data class CategorySearchResponse(
    val toolItem: List<ApiToolItem>
)

data class ToolCategory(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String
)

data class SearchResponse(
    val items: List<ToolItem>,
    val totalCount: Int
)

data class CategoryResponse(
    val categories: List<ToolCategory>
)

interface ToolSearchService {
    @GET("search")
    suspend fun searchTools(
        @Query("query") query: String,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<SearchResponse>

    @GET("categories")
    suspend fun getCategories(): Response<CategoryResponse>

    @GET("toolitem/getAllTool")
    suspend fun getAllTools(
        @Header("Authorization") token: String
    ): Response<List<ApiToolItem>>

    @GET("toolitem/search/tool/{name}")
    suspend fun searchToolByName(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("name") name: String
    ): Response<SearchToolResponse>

    @GET("toolitem/search/tool/category/{category}")
    suspend fun searchToolsByCategory(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("category") category: String
    ): Response<CategorySearchResponse>

    data class SearchToolResponse(
        val toolItem: ApiToolItem
    )

    companion object {
        fun create(): ToolSearchService {
            return ToolTrackApi.retrofitInstance().create(ToolSearchService::class.java)
        }
    }
}
