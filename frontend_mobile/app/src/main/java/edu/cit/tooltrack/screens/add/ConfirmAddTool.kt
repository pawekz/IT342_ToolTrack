package edu.cit.tooltrack.screens.add

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import edu.cit.tooltrack.MainActivity
import edu.cit.tooltrack.api.AddToolRequest
import edu.cit.tooltrack.api.ToolCreationService
import edu.cit.tooltrack.api.uploadImageInChunks
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import edu.cit.tooltrack.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

// Add the missing imports
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import edu.cit.tooltrack.R

class ConfirmAddToolActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var toolCreationService: ToolCreationService
    private var toolId: Int? = null
    private var qrCodeUrl: String? = null
    private var qrCodeBlob: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SessionManager and API service
        sessionManager = SessionManager(this)
        // Modify the service creation to accept SessionManager
        toolCreationService = ToolCreationService.create(sessionManager)

        // Get data from intent extras
        val toolName = intent.getStringExtra("TOOL_NAME") ?: "Drill Machine"
        val toolDescription = intent.getStringExtra("TOOL_DESCRIPTION") ?:
        "Power drill with adjustable speed, includes drill bits set."
        val toolCategory = intent.getStringExtra("TOOL_CATEGORY") ?: "Power Tools"
        val toolLocation = intent.getStringExtra("TOOL_LOCATION") ?: "Storage Room B"
        val toolCondition = intent.getStringExtra("TOOL_CONDITION") ?: "Excellent"
        val toolImageUri = intent.getStringExtra("TOOL_IMAGE_URI")
        val dateAcquired = intent.getStringExtra("DATE_ACQUIRED") ?:
        LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

        setContent {
            ToolTrackTheme {
                var isLoading by remember { mutableStateOf(false) }
                var showSuccessDialog by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }
                var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

                // Convert QR code blob to bitmap when available
                LaunchedEffect(qrCodeBlob) {
                    qrCodeBlob?.let {
                        withContext(Dispatchers.Default) {
                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                            qrCodeBitmap = bitmap
                        }
                    }
                }

                ConfirmAddToolScreen(
                    toolName = toolName,
                    toolDescription = toolDescription,
                    toolCategory = toolCategory,
                    toolLocation = toolLocation,
                    toolCondition = toolCondition,
                    toolImageUri = toolImageUri,
                    dateAcquired = dateAcquired,
                    onBackClick = { finish() },
                    onEditClick = { finish() }, // Go back to edit screen
                    onConfirmClick = {
                        // Start the tool creation process
                        isLoading = true

                        lifecycleScope.launch {
                            try {
                                // 1. Upload image if available
                                var imageUrl: String? = null
                                var imageName: String? = null

                                if (toolImageUri != null) {
                                    val imageUri = Uri.parse(toolImageUri)
                                    val imageFile = createTempFileFromUri(imageUri)

                                    if (imageFile != null) {
                                        // Compress the image
                                        val compressedFile = compressImage(imageFile)

                                        // Upload the image in chunks - now with sessionManager
                                        val uploadResponse = uploadImageInChunks(
                                            file = compressedFile,
                                            sessionManager = sessionManager
                                        )

                                        if (uploadResponse != null) {
                                            imageUrl = uploadResponse.imageUrl
                                            imageName = uploadResponse.image_name
                                            Log.d("ConfirmAddTool", "Image uploaded: $imageUrl")
                                        } else {
                                            throw Exception("Failed to upload image")
                                        }
                                    }
                                }

                                // 2. Create tool
                                val addToolRequest = AddToolRequest(
                                    name = toolName,
                                    description = toolDescription,
                                    category = toolCategory,
                                    location = toolLocation,
                                    date_acquired = convertDateStringToMap(dateAcquired), // Convert string to date map
                                    image_url = imageUrl ?: "",
                                    image_name = imageName ?: ""
                                )

                                // Get token from SessionManager
                                val token = sessionManager.fetchAuthToken() ?: ""
                                
                                // Add token parameter to addTool call
                                val addToolResponse = toolCreationService.addTool(
                                    token = token,
                                    toolRequest = addToolRequest
                                )

                                if (addToolResponse.isSuccessful && addToolResponse.body() != null) {
                                    val toolResponse = addToolResponse.body()!!
                                    toolId = toolResponse.toolId
                                    Log.d("ConfirmAddTool", "Tool added with ID: $toolId")

                                    // 3. Generate QR code - add token parameter
                                    val qrResponse = toolCreationService.generateQrCode(
                                        token = token,
                                        toolId = toolId!!
                                    )

                                    if (qrResponse.isSuccessful && qrResponse.body() != null) {
                                        // Get QR code as blob
                                        val qrCodeResponse = qrResponse.body()!!
                                        qrCodeUrl = qrCodeResponse.imageUrl

                                        // Download QR code as blob
                                        val qrBlob = downloadQrCode(qrCodeUrl!!)
                                        qrCodeBlob = qrBlob

                                        // 4. Convert QR code blob to file and upload it
                                        if (qrBlob != null) {
                                            // Create a temporary file for the QR code
                                            val qrCodeFile = File.createTempFile("qrcode_", ".png", cacheDir)
                                            FileOutputStream(qrCodeFile).use { outputStream ->
                                                outputStream.write(qrBlob)
                                            }

                                            // Upload QR code image - add token parameter
                                            val requestFile = qrCodeFile.asRequestBody("image/png".toMediaTypeOrNull())
                                            val filePart = MultipartBody.Part.createFormData("file", qrCodeFile.name, requestFile)

                                            val uploadQrResponse = toolCreationService.uploadQrCodeImage(
                                                token = token,
                                                filePart = filePart
                                            )

                                            if (uploadQrResponse.isSuccessful && uploadQrResponse.body() != null) {
                                                val uploadQrResult = uploadQrResponse.body()!!
                                                val qrImageUrl = uploadQrResult.imageUrl
                                                val qrImageName = uploadQrResult.image_name

                                                Log.d("ConfirmAddTool", "QR code uploaded: $qrImageUrl")

                                                // 5. Associate QR code with tool - add token parameter
                                                val addQrRequest = mapOf(
                                                    "image_url" to qrImageUrl,
                                                    "tool_id" to toolId!!,
                                                    "qr_code_name" to qrImageName
                                                )

                                                val associateResponse = toolCreationService.associateQrWithTool(
                                                    token = token,
                                                    params = addQrRequest
                                                )

                                                if (associateResponse.isSuccessful && associateResponse.body() != null) {
                                                    Log.d("ConfirmAddTool", "QR code associated with tool")
                                                } else {
                                                    Log.w("ConfirmAddTool", "Failed to associate QR code: ${associateResponse.errorBody()?.string()}")
                                                }
                                            } else {
                                                Log.w("ConfirmAddTool", "Failed to upload QR code: ${uploadQrResponse.errorBody()?.string()}")
                                            }

                                            // Clean up temporary file
                                            qrCodeFile.delete()
                                        }

                                        // Show success dialog
                                        showSuccessDialog = true
                                    } else {
                                        throw Exception("Failed to generate QR code: ${qrResponse.errorBody()?.string()}")
                                    }
                                } else {
                                    throw Exception("Failed to add tool: ${addToolResponse.errorBody()?.string()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ConfirmAddTool", "Error adding tool", e)
                                errorMessage = e.message
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )

                // Show loading overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .width(200.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = TealPrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Adding Tool...",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Show error message
                if (errorMessage != null) {
                    AlertDialog(
                        onDismissRequest = { errorMessage = null },
                        title = { Text("Error") },
                        text = { Text(errorMessage ?: "An unknown error occurred") },
                        confirmButton = {
                            Button(onClick = { errorMessage = null }) {
                                Text("OK")
                            }
                        }
                    )
                }

                // Show success dialog
                if (showSuccessDialog) {
                    SuccessDialog(
                        qrCodeBitmap = qrCodeBitmap,
                        onAddAnother = {
                            // Navigate back to AddToolActivity
                            val intent = Intent(this@ConfirmAddToolActivity, AddToolActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onGoToHome = {
                            // Navigate to HomeScreen
                            val intent = Intent(this@ConfirmAddToolActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        },
                        onDismiss = {
                            showSuccessDialog = false
                        }
                    )
                }
            }
        }
    }

    // Create a temporary file from a URI
    private fun createTempFileFromUri(uri: Uri): File? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            return tempFile
        } catch (e: Exception) {
            Log.e("ConfirmAddTool", "Error creating temp file", e)
            return null
        }
    }

    // Compress an image file
    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val compressedFile = File.createTempFile("compressed_", ".jpg", cacheDir)

        // Calculate new dimensions while maintaining aspect ratio
        val maxDimension = 1024
        val width = bitmap.width
        val height = bitmap.height

        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxDimension
            newHeight = (height * maxDimension.toFloat() / width).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (width * maxDimension.toFloat() / height).toInt()
        }

        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        // Compress and save
        val outputStream = FileOutputStream(compressedFile)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        return compressedFile
    }

    // Download QR code as blob
    private suspend fun downloadQrCode(url: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = java.net.URL(url).openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, len)
                }
                byteArrayOutputStream.toByteArray()
            } catch (e: Exception) {
                Log.e("ConfirmAddTool", "Error downloading QR code", e)
                null
            }
        }
    }

    // Helper function to convert date string to Map format for API
    private fun convertDateStringToMap(dateString: String): Map<String, Int> {
        try {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val date = LocalDate.parse(dateString, formatter)

            return mapOf(
                "year" to date.year,
                "month" to date.monthValue,
                "day" to date.dayOfMonth
            )
        } catch (e: Exception) {
            Log.e("ConfirmAddTool", "Error parsing date: $dateString", e)
            // Return current date as fallback
            val now = LocalDate.now()
            return mapOf(
                "year" to now.year,
                "month" to now.monthValue,
                "day" to now.dayOfMonth
            )
        }
    }
}

// Define Figma colors - same as AddToolActivity for consistency
private val TealPrimary = Color(0xFF2EA69E)
private val LightTeal = Color(0xFFE7F6F4)
private val DarkGrey = Color(0xFF2E3A59)
private val LightGrey = Color(0xFFF8F9FC)
private val MediumGrey = Color(0xFF8F9BB3)
private val BorderGrey = Color(0xFFEDF1F7)
private val SuccessGreen = Color(0xFF00E096)

@Composable
fun ConfirmAddToolScreen(
    toolName: String,
    toolDescription: String,
    toolCategory: String,
    toolLocation: String,
    toolCondition: String,
    toolImageUri: String? = null,
    dateAcquired: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = LightTeal,
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(top = 20.dp, bottom = 20.dp)
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(32.dp)
                        .background(
                            color = Color(0xB3E7F6F4), // Semi-transparent light teal
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(onClick = onBackClick)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkGrey,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.Center)
                    )
                }

                // Title
                Text(
                    text = "Confirm Tool Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkGrey,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        containerColor = Color.White // Set background to white
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tool Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightGrey)
                    .border(
                        width = 1.dp,
                        color = BorderGrey,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (toolImageUri != null) {
                    // Display the actual image if available
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(toolImageUri)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Tool Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Display placeholder
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Circle with camera icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, BorderGrey, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_gallery),
                                contentDescription = "Tool Image",
                                modifier = Modifier.size(24.dp),
                                tint = TealPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "QR Code not available",
                            color = MediumGrey,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Tool Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        text = "Tool Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkGrey
                    )

                    // Tool Name
                    DetailItem(label = "Tool Name", value = toolName)

                    // Description
                    DetailItem(label = "Description", value = toolDescription)

                    // Category 
                    DetailItem(label = "Category", value = toolCategory)

                    // Condition
                    DetailItem(label = "Condition", value = toolCondition)

                    // Location
                    DetailItem(label = "Location", value = toolLocation)

                    // Date Acquired
                    DetailItem(label = "Date Acquired", value = dateAcquired)
                }
            }

            // Summary Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightTeal.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Ready",
                        tint = TealPrimary,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The tool is ready to be added to the inventory",
                        fontSize = 14.sp,
                        color = DarkGrey,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Edit Button
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(TealPrimary)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TealPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Edit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Confirm Button
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Confirm",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label
        Text(
            text = label,
            fontSize = 12.sp,
            color = MediumGrey,
            fontWeight = FontWeight.Medium
        )

        // Value
        Text(
            text = value,
            fontSize = 14.sp,
            color = DarkGrey,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SuccessDialog(
    qrCodeBitmap: Bitmap?,
    onAddAnother: () -> Unit,
    onGoToHome: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success icon
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = SuccessGreen,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Success message
                Text(
                    text = "Tool Added Successfully",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGrey,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Would you like to add a new tool?",
                    fontSize = 16.sp,
                    color = MediumGrey,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // QR Code display
                if (qrCodeBitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, BorderGrey, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(qrCodeBitmap),
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "QR Code Generated",
                        fontSize = 14.sp,
                        color = TealPrimary,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    CircularProgressIndicator(color = TealPrimary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Generating QR Code...",
                        fontSize = 14.sp,
                        color = MediumGrey
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Add another tool button
                    Button(
                        onClick = onAddAnother,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Add another tool")
                    }

                    // Go to home button
                    Button(
                        onClick = onGoToHome,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = TealPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(TealPrimary)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Go to Home")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Close button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Close",
                        color = MediumGrey
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmAddToolScreenPreview() {
    ToolTrackTheme {
        ConfirmAddToolScreen(
            toolName = "Drill Machine",
            toolDescription = "Power drill with adjustable speed, includes drill bits set.",
            toolCategory = "Power Tools",
            toolLocation = "Storage Room B",
            toolCondition = "Excellent",
            dateAcquired = "05/15/2023",
            onBackClick = {},
            onEditClick = {},
            onConfirmClick = {}
        )
    }
}

@Preview
@Composable
fun SuccessDialogPreview() {
    ToolTrackTheme {
        SuccessDialog(
            qrCodeBitmap = null,
            onAddAnother = {},
            onGoToHome = {},
            onDismiss = {}
        )
    }
}
