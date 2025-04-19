package edu.cit.tooltrack.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class RegistrationRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password_hash: String,
    val isGoogle: Boolean = false,
    val role: String = "staff",
    val is_active: Int = 1
)

data class RegistrationResponse(
    val message: String,
    val user_id: Int
)

interface ToolTrackApi {
    @POST("register")
    suspend fun registerUser(@Body request: RegistrationRequest): Response<RegistrationResponse>

    companion object {
        private const val BASE_URL =
            "https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/"

        fun create(): ToolTrackApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ToolTrackApi::class.java)
        }
    }
}