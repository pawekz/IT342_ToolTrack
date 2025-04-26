package edu.cit.tooltrack.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.time.LocalDate

data class BorrowedToolResponse(
    val id: Int,
    val tool_id: Int,
    val tool_name: String,
    val tool_image_url: String?,
    val borrowed_date: String,
    val expected_return_date: String,
    val actual_return_date: String?,
    val status: String // "active", "overdue", "returned"
)

interface BorrowedToolApi {
    @GET("borrows/user")
    suspend fun getUserBorrowedTools(
        @Header("Authorization") token: String
    ): Response<List<BorrowedToolResponse>>
    
    @GET("borrows/{borrowId}")
    suspend fun getBorrowedToolDetails(
        @Header("Authorization") token: String,
        @Path("borrowId") borrowId: Int
    ): Response<BorrowedToolResponse>
    
    companion object {
        fun create(): BorrowedToolApi {
            return ToolTrackApi.retrofitInstance().create(BorrowedToolApi::class.java)
        }
    }
}
