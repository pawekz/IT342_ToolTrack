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
    val category: String,
    val name: String,
    val qr_code: String = "",
    val qr_code_name: String = "",
    val location: String,
    val description: String,
    val date_acquired: Map<String, Int> = emptyMap(), // Changed to Map to match API
    val image_url: String = "",
    val image_name: String = "",
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
    @FormUrlEncoded
    @POST("toolitem/upload")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("size") size: String,
        @Field("currentChunkIndex") currentChunkIndex: String,
        @Field("totalChunks") totalChunks: String,
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

    @FormUrlEncoded
    @POST("qrcode/uploadImage")
    suspend fun uploadQrImage(
        @Header("Authorization") token: String,
        @Field("file") file: String,
        @Field("toolId") toolId: String
    ): Response<QrCodeUploadResponse>

    @FormUrlEncoded
    @PUT("toolitem/addQr")
    suspend fun addQrToTool(
        @Header("Authorization") token: String,
        @Field("image_url") imageUrl: String,
        @Field("tool_id") toolId: Int,
        @Field("qr_code_name") qrCodeName: String
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
        return api.addTool(token, toolRequest)
    }

    suspend fun generateQrCode(token: String, toolId: Int): Response<QrCodeUploadResponse> {
        val request = QrCodeCreateRequest(toolId = toolId)
        return api.createQrCode(token, toolId, request)
    }

    suspend fun uploadQrCodeImage(token: String, filePart: MultipartBody.Part): Response<QrCodeUploadResponse> {
        // This method needs to be updated to match the API's requirements
        // For now, returning a placeholder implementation
        val fileName = filePart.headers?.get("Content-Disposition")?.substringAfter("filename=")?.trim('"') ?: "unknown"
        return api.uploadQrImage(token, fileName, "0")
    }

    suspend fun associateQrWithTool(token: String, params: Map<String, Any>): Response<AddQrResponse> {
        val imageUrl = params["image_url"] as? String ?: ""
        val toolId = params["tool_id"] as? Int ?: 0
        val qrCodeName = params["qr_code_name"] as? String ?: ""

        return api.addQrToTool(token, imageUrl, toolId, qrCodeName)
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
        // For simplicity, we're not actually chunking the file in this implementation
        // In a real implementation, you would read the file in chunks and upload each chunk
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val response = api.uploadImage(
            token = token,
            name = file.name,
            size = fileSize.toString(),
            currentChunkIndex = "0",
            totalChunks = totalChunks.toString(),
            file = filePart
        )

        if (response.isSuccessful) {
            return response.body()
        } else {
            Log.e("ToolCreationService", "Error uploading image: ${response.errorBody()?.string()}")
            return null
        }
    } catch (e: Exception) {
        Log.e("ToolCreationService", "Exception uploading image", e)
        return null
    }
}

// Overloaded function that accepts SessionManager instead of token
suspend fun uploadImageInChunks(
    file: File,
    sessionManager: edu.cit.tooltrack.utils.SessionManager,
    chunkSize: Int = 5 * 1024 * 1024 // 5MB chunks
): ImageUploadResponse? {
    // Extract token from SessionManager
    val token = sessionManager.fetchAuthToken() ?: ""
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
