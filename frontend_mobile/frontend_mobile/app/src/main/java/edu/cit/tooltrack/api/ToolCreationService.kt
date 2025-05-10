package edu.cit.tooltrack.api

import android.util.Log
import edu.cit.tooltrack.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

// Data classes for requests and responses
data class AddToolRequest(
    val tool_id: Int = 0,
    val toolTransaction: List<Any> = emptyList(),
    val tool_condition: String = "NEW",
    val status: String = "AVAILABLE",
    val category: String,
    val name: String,
    val qr_code: String = "",
    val qr_code_name: String = "",
    val location: String,
    val description: String,
    val date_acquired: Map<String, Int> = emptyMap(),
    val image_url: String = "",
    val image_name: String = "",
    val serial_number: String = "",
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
    val imageName: String
)

data class QrCodeCreateRequest(
    val toolId: Int
)

data class QrCodeUploadResponse(
    val imageUrl: String,
    val message: String
)

data class AddQrResponse(
    val message: String
)

// API interface for tool creation
interface ToolCreationApi {
    // Step 1: Upload tool image
    @Multipart
    @POST("toolitem/upload")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Query("name") name: String,
        @Query("size") size: String,
        @Query("currentChunkIndex") currentChunkIndex: String,
        @Query("totalChunks") totalChunks: String,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    // Step 2: Add tool data
    @POST("toolitem/addTool")
    suspend fun addTool(
        @Header("Authorization") token: String,
        @Body toolRequest: AddToolRequest
    ): Response<AddToolResponse>

    // Step 3: Generate QR code
    @POST("qrcode/create/{toolId}")
    suspend fun createQrCode(
        @Header("Authorization") token: String,
        @Path("toolId") toolId: Int
    ): Response<QrCodeUploadResponse>

    // Step 4: Upload QR code image to S3
    @Multipart
    @POST("qrcode/uploadImage")
    suspend fun uploadQrImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Query("toolId") toolId: String
    ): Response<QrCodeUploadResponse>

    // Step 5: Associate QR code with tool
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

    // Step 1: Upload tool image
    suspend fun uploadImage(token: String, file: File): Response<ImageUploadResponse> {
        val fileSize = file.length()
        
        // Create a MultipartBody.Part from the file
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        
        // Log the token for debugging
        Log.d("ToolCreationService", "Uploading image with token: Bearer $token")
        
        return api.uploadImage(
            token = "Bearer $token",
            name = file.name,
            size = fileSize.toString(),
            currentChunkIndex = "0",
            totalChunks = "1",
            file = filePart
        )
    }

    // Step 2: Add tool data
    suspend fun addTool(token: String, toolRequest: AddToolRequest): Response<AddToolResponse> {
        // Log the token and request for debugging
        Log.d("ToolCreationService", "Adding tool with token: Bearer $token")
        Log.d("ToolCreationService", "Tool request: $toolRequest")
        
        return api.addTool("Bearer $token", toolRequest)
    }

    // Step 3: Generate QR code
    suspend fun generateQrCode(token: String, toolId: Int): Response<QrCodeUploadResponse> {
        Log.d("ToolCreationService", "Generating QR code with token: Bearer $token")
        return api.createQrCode("Bearer $token", toolId)
    }

    // Step 4: Upload QR code image to S3
    suspend fun uploadQrCodeImage(token: String, fileName: String, toolId: String): Response<QrCodeUploadResponse> {
        // Create a dummy file with the fileName
        val tempFile = File.createTempFile("qr_", ".png")
        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)
        
        Log.d("ToolCreationService", "Uploading QR code with token: Bearer $token")
        
        return api.uploadQrImage("Bearer $token", filePart, toolId)
    }

    // Step 5: Associate QR code with tool
    suspend fun associateQrWithTool(token: String, imageUrl: String, toolId: Int, qrCodeName: String): Response<AddQrResponse> {
        Log.d("ToolCreationService", "Associating QR code with token: Bearer $token")
        return api.addQrToTool("Bearer $token", imageUrl, toolId, qrCodeName)
    }

    companion object {
        fun create(): ToolCreationService {
            val api = ToolCreationApi.create()
            return ToolCreationService(api)
        }

        fun create(sessionManager: SessionManager): ToolCreationService {
            val api = ToolCreationApi.create()
            return ToolCreationService(api)
        }
    }
}
