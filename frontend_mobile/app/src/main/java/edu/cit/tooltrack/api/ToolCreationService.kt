package edu.cit.tooltrack.api

import android.graphics.Bitmap
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import java.util.*

// Data classes for requests and responses
data class AddToolRequest(
    val tool_id: Int = 0,
    val toolTransaction: List<Any> = emptyList(),
    val tool_condition: String,
    val status: String = "AVAILABLE",
    val category: String,
    val name: String,
    val qr_code: String = "",
    val qr_code_name: String = "",
    val location: String,
    val description: String,
    val date_acquired: Map<String, Int> = emptyMap(), // Changed to Map to match API
    val image_url: String = "",
    val image_name: String = "",
    val serial_number: String = "", // Added optional serial number field
    val created_at: Map<String, Int> = emptyMap(),
    val updated_at: Map<String, Int> = emptyMap(),
    val is_active: Boolean = true
)

data class AddToolResponse(
    val toolId: Int,
    val message: String
)

data class ImageUploadResponse(
    val imageUrl: String,
    val image_name: String
)

data class QrCodeCreateRequest(
    val toolId: Int,
    val qrCodeName: String = "",
    val imageUrl: String = ""
)

data class QrCodeUploadResponse(
    val imageUrl: String,
    val image_name: String
)

data class AddQrResponse(
    val message: String
)

// API interface for tool creation
interface ToolCreationApi {
    @POST("toolitem/upload")
    @Multipart
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("size") size: String,
        @Query("currentChunkIndex") currentChunkIndex: String,
        @Query("totalChunks") totalChunks: String,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @POST("toolitem/addTool")
    suspend fun addTool(
        @Header("Authorization") token: String,
        @Body toolRequest: AddToolRequest
    ): Response<AddToolResponse>

    @POST("qrcode/create/{toolId}")
    suspend fun createQrCode(
        @Header("Authorization") token: String,
        @Path("toolId") toolId: Int,
        @Body request: QrCodeCreateRequest
    ): Response<QrCodeUploadResponse>

    @POST("qrcode/uploadImage")
    @Multipart
    suspend fun uploadQrImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Query("toolId") toolId: String
    ): Response<QrCodeUploadResponse>

    @PUT("toolitem/addQr")
    suspend fun addQrToTool(
        @Header("Authorization") token: String,
        @Query("image_url") imageUrl: String,
        @Query("tool_id") toolId: Int,
        @Query("qr_code_name") qrCodeName: String
    ): Response<AddQrResponse>

    companion object {
        fun create(): ToolCreationApi {
            return ToolTrackApi.retrofitInstance().create(ToolCreationApi::class.java)
        }
    }
}

// Service class that wraps the API interface
class ToolCreationService private constructor(private val api: ToolCreationApi) {

    suspend fun addTool(token: String, toolRequest: AddToolRequest): Response<AddToolResponse> {
        val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return api.addTool(authToken, toolRequest)
    }

    suspend fun addTool(sessionManager: edu.cit.tooltrack.utils.SessionManager, toolRequest: AddToolRequest): Response<AddToolResponse> {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("Authentication token is expired or missing. Please log in again.")
        }
        return addTool(token, toolRequest)
    }

    suspend fun generateQrCode(token: String, toolId: Int): Response<QrCodeUploadResponse> {
        val request = QrCodeCreateRequest(toolId = toolId)
        val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return api.createQrCode(authToken, toolId, request)
    }

    suspend fun generateQrCode(sessionManager: edu.cit.tooltrack.utils.SessionManager, toolId: Int): Response<QrCodeUploadResponse> {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("Authentication token is expired or missing. Please log in again.")
        }
        return generateQrCode(token, toolId)
    }

    suspend fun uploadQrCodeImage(token: String, file: File, toolId: String): Response<QrCodeUploadResponse> {
        // Check if file exists and is readable
        if (!file.exists() || !file.canRead()) {
            Log.e("ToolCreationService", "QR code file does not exist or is not readable: ${file.absolutePath}")
            throw Exception("QR code file cannot be accessed")
        }

        // Create request body from file
        val requestBody = file.asRequestBody("image/png".toMediaTypeOrNull())

        // Create MultipartBody.Part from request body
        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestBody
        )

        // Call the API with the MultipartBody.Part
        val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return api.uploadQrImage(authToken, filePart, toolId)
    }

    suspend fun uploadQrCodeImage(sessionManager: edu.cit.tooltrack.utils.SessionManager, file: File, toolId: String): Response<QrCodeUploadResponse> {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("Authentication token is expired or missing. Please log in again.")
        }
        return uploadQrCodeImage(token, file, toolId)
    }

    suspend fun associateQrWithTool(token: String, params: Map<String, Any>): Response<AddQrResponse> {
        val imageUrl = params["image_url"] as? String ?: ""
        val toolId = params["tool_id"] as? Int ?: 0
        val qrCodeName = params["qr_code_name"] as? String ?: ""

        val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return api.addQrToTool(authToken, imageUrl, toolId, qrCodeName)
    }

    suspend fun associateQrWithTool(sessionManager: edu.cit.tooltrack.utils.SessionManager, params: Map<String, Any>): Response<AddQrResponse> {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("Authentication token is expired or missing. Please log in again.")
        }
        return associateQrWithTool(token, params)
    }

    companion object {
        fun create(): ToolCreationService {
            val api = ToolCreationApi.create()
            return ToolCreationService(api)
        }

        // Overloaded create method that accepts SessionManager
        fun create(sessionManager: edu.cit.tooltrack.utils.SessionManager): ToolCreationService {
            val api = ToolCreationApi.create()
            return ToolCreationService(api)
        }
    }
}

// Helper function to upload image in chunks
suspend fun uploadImageInChunks(
    file: File,
    token: String,
    chunkSize: Int = 5 * 1024 * 1024 // 5MB chunks
): ImageUploadResponse? {
    val api = ToolCreationApi.create()
    val fileSize = file.length()
    val totalChunks = (fileSize / chunkSize).toInt() + if (fileSize % chunkSize > 0) 1 else 0

    try {
        // Verify file exists and is readable
        if (!file.exists() || !file.canRead()) {
            Log.e("ToolCreationService", "File does not exist or is not readable: ${file.absolutePath}")
            throw Exception("Image file cannot be accessed")
        }

        // For simplicity, we're not actually chunking the file in this implementation
        // In a real implementation, you would read the file in chunks and upload each chunk
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        // Create MultipartBody.Part from request body
        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestBody
        )

        Log.d("ToolCreationService", "Uploading image: ${file.name}, size: $fileSize bytes")

        val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        val response = api.uploadImage(
            token = authToken,
            name = file.name,
            size = fileSize.toString(),
            currentChunkIndex = "0",
            totalChunks = totalChunks.toString(),
            file = filePart
        )

        if (response.isSuccessful) {
            Log.d("ToolCreationService", "Image upload successful")
            return response.body()
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            Log.e("ToolCreationService", "Error uploading image: $errorBody")
            throw Exception("Server error uploading image: $errorBody")
        }
    } catch (e: Exception) {
        Log.e("ToolCreationService", "Exception uploading image", e)
        throw e
    }
}

// Overloaded function that accepts SessionManager instead of token
suspend fun uploadImageInChunks(
    file: File,
    sessionManager: edu.cit.tooltrack.utils.SessionManager,
    chunkSize: Int = 5 * 1024 * 1024 // 5MB chunks
): ImageUploadResponse? {
    // Extract token from SessionManager
    val token = sessionManager.fetchAuthToken()
    if (token.isNullOrEmpty()) {
        throw Exception("Authentication token is expired or missing. Please log in again.")
    }
    return uploadImageInChunks(file, token, chunkSize)
}

// Helper function to convert Bitmap to File
fun bitmapToFile(bitmap: Bitmap, fileName: String = "qr_code_${UUID.randomUUID()}.png"): File {
    val file = File.createTempFile(fileName, null)
    file.deleteOnExit()

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    val bitmapData = bos.toByteArray()

    file.outputStream().use { it.write(bitmapData) }
    return file
}
