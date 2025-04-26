package edu.cit.tooltrack.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class ToolItem(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val status: String,
    val categoryId: Int,
    val categoryName: String
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
    
    companion object {
        fun create(): ToolSearchService {
            return ToolTrackApi.retrofitInstance().create(ToolSearchService::class.java)
        }
    }
}
