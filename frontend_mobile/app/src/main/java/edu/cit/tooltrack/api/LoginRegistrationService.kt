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
        val role: String = "staff",
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
        @POST("register")
        suspend fun registerUser(@Body request: RegistrationRequest): Response<String>

        @POST("login")
        suspend fun loginUser(@Body request: LoginRequest): Response<String>

        companion object {
            private const val BASE_URL =
                "https://tooltrack-backend-edbxg7crbfbuhha8.southeastasia-01.azurewebsites.net/"

            fun create(): ToolTrackApi {
                // Create logging interceptor
                val logging = HttpLoggingInterceptor { message ->
                    Log.d("API_DEBUG", message)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                // Create OkHttp client with logging
                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build()

                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(ToolTrackApi::class.java)
            }
        }
    }