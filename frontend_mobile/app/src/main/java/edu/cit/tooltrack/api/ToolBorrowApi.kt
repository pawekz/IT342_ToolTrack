package edu.cit.tooltrack.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

data class ToolBorrowResponse(
    val toolItem: ToolBorrowItem
)

data class ToolBorrowItem(
    val tool_id: Int,
    val category: String?,
    val name: String,
    val qr_code: String,
    val location: String,
    val description: String,
    val date_acquired: String,
    val image_url: String,
    val created_at: String,
    val updated_at: String,
    val tool_condition: String? = null,
    val status: String? = null
)

interface ToolBorrowApi {
    @GET("toolitem/borrow/{toolId}")
    suspend fun getToolForBorrow(
        @Header("Authorization") token: String,
        @Path("toolId") toolId: Int
    ): Response<ToolBorrowResponse>

    companion object {
        fun create(): ToolBorrowApi {
            return ToolTrackApi.retrofitInstance().create(ToolBorrowApi::class.java)
        }
    }
}
