package edu.cit.tooltrack.api

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class RegistrationRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password_hash: String,
    val isGoogle: Boolean = false,
    val role: String = "Staff",  // Changed from "staff" to "Staff" (case-sensitive)
    val is_active: Int = 1
)

data class RegistrationResponse(
    val message: String,
    val user_id: Int
)

data class TokenResponse(
    val token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

interface ToolTrackApi {
    @POST("auth/user/register")
    suspend fun registerUser(@Body request: RegistrationRequest): Response<String>

    @POST("auth/v2/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<TokenResponse>

    companion object {
        private const val BASE_URL =
            "https://backend-tooltrack-pe3u8.ondigitalocean.app/"

        private val retrofit by lazy {
            // Create logging interceptor
            val logging = HttpLoggingInterceptor { message ->
                Log.d("API_DEBUG", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Create OkHttp client with logging
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        fun create(): ToolTrackApi {
            return retrofit.create(ToolTrackApi::class.java)
        }

        // Renamed to avoid platform declaration clash with the Kotlin property accessor
        fun retrofitInstance(): Retrofit {
            return retrofit
        }
    }
}
