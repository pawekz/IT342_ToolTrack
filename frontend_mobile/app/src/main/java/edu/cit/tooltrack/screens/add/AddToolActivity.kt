package edu.cit.tooltrack.screens.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddToolActivity : ComponentActivity() {
    lateinit var photoFile: File
    private var photoUri: Uri? = null

    // Create a temporary file for camera photos
    fun createImageFile(): File {
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        ).apply {
            photoFile = this
        }
    }

    // Get URI for the photo file
    fun getPhotoUri(): Uri {
        return FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            photoFile
        ).also {
            photoUri = it
        }
    }

    // Create a temporary file from a URI
    fun createTempFileFromUri(uri: Uri): File? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)

            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()

            return tempFile
        } catch (e: Exception) {
            Log.e("AddToolActivity", "Error creating temp file", e)
            return null
        }
    }

    // Compress an image file
    fun compressImage(file: File): File {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToolTrackTheme {
                AddToolScreen(
                    onBackClick = { finish() },
                    onNextClick = { toolData ->
                        // Navigate to ConfirmAddToolActivity with tool data
                        val intent = Intent(this, ConfirmAddToolActivity::class.java).apply {
                            putExtra("TOOL_NAME", toolData.name)
                            putExtra("TOOL_DESCRIPTION", toolData.description)
                            putExtra("TOOL_CATEGORY", toolData.category)
                            putExtra("TOOL_LOCATION", toolData.location)
                            putExtra("TOOL_IMAGE_URI", toolData.imageUri?.toString())
                            putExtra("DATE_ACQUIRED", toolData.dateAcquired)
                        }
                        startActivity(intent)
                    },
                    createImageFile = { createImageFile() },
                    getPhotoUri = { getPhotoUri() }
                )
            }
        }
    }
}

// Data class to hold tool information
data class ToolData(
    val name: String,
    val description: String,
    val category: String,
    val location: String,
    val imageUri: Uri?,
    val dateAcquired: String
)

class OnBackClickPreviewProvider : PreviewParameterProvider<() -> Unit> {
    override val values = sequenceOf({ /* Do nothing for preview */ })
}

// Define Figma colors
private val TealPrimary = Color(0xFF2EA69E)
private val LightTeal = Color(0xFFE7F6F4)
private val DarkGrey = Color(0xFF2E3A59)
private val LightGrey = Color(0xFFF8F9FC)
private val MediumGrey = Color(0xFF8F9BB3)
private val BorderGrey = Color(0xFFEDF1F7)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddToolScreen(
    @PreviewParameter(OnBackClickPreviewProvider::class) onBackClick: () -> Unit,
    onNextClick: (ToolData) -> Unit = {},
    createImageFile: () -> File = { File("") },
    getPhotoUri: () -> Uri = { Uri.EMPTY }
) {
    val context = LocalContext.current
    var toolName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var expandedLocation by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("") }
    var expandedCondition by remember { mutableStateOf(false) }
    var selectedCondition by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageOptions by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val categories = listOf(
        "Power Tools", 
        "Hand Tools", 
        "Garden Tools", 
        "Electrical Tools", 
        "Painting", 
        "Automotive Tools", 
        "Measuring Tools", 
        "Safety Equipment"
    )

    val locations = listOf(
        "Tool Area A",
        "Tool Area B",
        "Equipment Area",
        "Workshop Zone",
        "Storage Room 1",
        "Storage Room 2",
        "Maintenance Bay"
    )

    val conditions = listOf("Excellent", "Good", "Fair", "Poor")

    // Camera launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Log.d("AddToolActivity", "Picture taken successfully")

            // Compress the image in a background thread
            (context as? AddToolActivity)?.let { activity ->
                Thread {
                    try {
                        // Get the file from the URI
                        val photoFile = activity.photoFile

                        // Compress the image
                        val compressedFile = activity.compressImage(photoFile)

                        // Get URI for the compressed file
                        val compressedUri = FileProvider.getUriForFile(
                            activity,
                            "${activity.applicationContext.packageName}.provider",
                            compressedFile
                        )

                        // Update the imageUri on the main thread
                        activity.runOnUiThread {
                            imageUri = compressedUri
                            Log.d("AddToolActivity", "Camera image compressed: $compressedUri")
                        }
                    } catch (e: Exception) {
                        Log.e("AddToolActivity", "Error compressing camera image", e)
                    }
                }.start()
            }
        } else {
            Log.d("AddToolActivity", "Failed to take picture")
        }
    }

    // Gallery launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Set the original URI first for immediate UI feedback
            imageUri = it
            Log.d("AddToolActivity", "Image selected from gallery: $it")

            // Compress the image in a background thread
            (context as? AddToolActivity)?.let { activity ->
                Thread {
                    try {
                        // Create a temporary file from the URI
                        val tempFile = activity.createTempFileFromUri(it)
                        if (tempFile != null) {
                            // Compress the image
                            val compressedFile = activity.compressImage(tempFile)

                            // Get URI for the compressed file
                            val compressedUri = FileProvider.getUriForFile(
                                activity,
                                "${activity.applicationContext.packageName}.provider",
                                compressedFile
                            )

                            // Update the imageUri on the main thread
                            activity.runOnUiThread {
                                imageUri = compressedUri
                                Log.d("AddToolActivity", "Image compressed: $compressedUri")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AddToolActivity", "Error compressing image", e)
                    }
                }.start()
            }
        }
    }

    // Function to take picture
    fun takePicture() {
        try {
            // Create the image file first to initialize photoFile
            createImageFile()
            val uri = getPhotoUri()
            imageUri = uri
            takePictureLauncher.launch(uri)
        } catch (e: Exception) {
            Log.e("AddToolActivity", "Error taking picture", e)
        }
    }

    // Permission launcher for camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePicture()
        } else {
            // Show message that permission is required
            Log.d("AddToolActivity", "Camera permission denied")
        }
    }

    // Function to check camera permission and take picture
    fun checkCameraPermissionAndTakePicture() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) -> {
                // Permission already granted, take picture
                takePicture()
            }
            else -> {
                // Request permission
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Function to pick image from gallery
    fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    // Show image options dialog
    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = { Text("Select Image Source") },
            text = { Text("Choose where to get the image from") },
            confirmButton = {
                Button(
                    onClick = {
                        showImageOptions = false
                        checkCameraPermissionAndTakePicture()
                    }
                ) {
                    Text("Camera")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showImageOptions = false
                        pickImageFromGallery()
                    }
                ) {
                    Text("Gallery")
                }
            }
        )
    }

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
            )

            {
                Spacer(modifier = Modifier.height(8.dp))
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
                    text = "Add Tool",
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

            // Image Upload Section
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
                    )
                    .clickable { showImageOptions = true },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    // Display the selected image
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .scale(Scale.FILL)
                                .build()
                        ),
                        contentDescription = "Selected Tool Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Add an overlay with a change button
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Button(
                            onClick = { showImageOptions = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.8f),
                                contentColor = DarkGrey
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Change Image")
                        }
                    }
                } else {
                    // Show upload prompt
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
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Upload Image",
                                modifier = Modifier.size(24.dp),
                                tint = TealPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Upload Tool Image",
                            color = TealPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap to take a photo or select from gallery",
                            color = MediumGrey,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }


            // Tool Name Input
            CustomInputField(
                value = toolName,
                onValueChange = { toolName = it },
                label = "Tool Name",
                placeholder = "Enter tool name"
            )

            Column {
                CustomInputField(
                    value = description,
                    onValueChange = { if (it.length <= 200) description = it },
                    label = "Description",
                    placeholder = "Enter a short description",
                    singleLine = false,
                    minHeight = 80.dp
                )
                Text(
                    text = "${description.length}/200",
                    fontSize = 12.sp,
                    color = MediumGrey,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            // Category Dropdown
            CustomDropdownField(
                selectedValue = selectedCategory,
                label = "Category",
                placeholder = "Select category",
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it },
                options = categories,
                onOptionSelected = { selectedCategory = it }
            )

            // Location Dropdown
            CustomDropdownField(
                selectedValue = selectedLocation,
                label = "Location",
                placeholder = "Select location",
                expanded = expandedLocation,
                onExpandedChange = { expandedLocation = it },
                options = locations,
                onOptionSelected = { selectedLocation = it }
            )

            /*// Condition Dropdown
            CustomDropdownField(
                selectedValue = selectedCondition,
                label = "Condition",
                placeholder = "Select condition",
                expanded = expandedCondition,
                onExpandedChange = { expandedCondition = it },
                options = conditions,
                onOptionSelected = { selectedCondition = it }
            )*/

            // Date Acquired Picker
            CustomDateField(
                label = "Date Acquired",
                date = selectedDate,
                onDateClick = { showDatePicker = true }
            )


            Spacer(modifier = Modifier.height(24.dp))

            // Next Button
            Button(
                onClick = {
                    // Validate inputs
                    if (toolName.isBlank()) {
                        // Show error for tool name
                        return@Button
                    }

                    if (selectedCategory.isBlank()) {
                        // Show error for category
                        return@Button
                    }

                    /*if (selectedCondition.isBlank()) {
                        // Show error for condition
                        return@Button
                    }*/

                    if (selectedLocation.isBlank()) {
                        // Show error for location
                        return@Button
                    }

                    // Create tool data object
                    val toolData = ToolData(
                        name = toolName,
                        description = description,
                        category = selectedCategory,
                        location = selectedLocation,
                        imageUri = imageUri,
                        dateAcquired = selectedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                    )

                    // Set loading state
                    isLoading = true

                    // Call next callback
                    onNextClick(toolData)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date Picker Dialog
            if (showDatePicker) {
                // Use a more explicit date picker state initialization
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                )

                // Use a dialog to ensure proper display
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            // Handle the selected date more explicitly
                            datePickerState.selectedDateMillis?.let { millis ->
                                val instant = java.time.Instant.ofEpochMilli(millis)
                                val localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                                // Update the selected date
                                selectedDate = localDate
                                Log.d("DatePicker", "Selected date: $localDate")
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState
                    )
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean = true,
    minHeight: Dp = 56.dp
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGrey,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    placeholder, 
                    color = MediumGrey,
                    fontSize = 14.sp
                ) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .border(
                    width = 1.dp,
                    color = BorderGrey,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = TealPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = singleLine
        )
    }
}

@Composable
fun CustomDropdownField(
    selectedValue: String,
    label: String,
    placeholder: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGrey,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                placeholder = { 
                    Text(
                        placeholder, 
                        color = MediumGrey,
                        fontSize = 14.sp
                    ) 
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        tint = MediumGrey,
                        modifier = Modifier.clickable { onExpandedChange(true) }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .border(
                        width = 1.dp,
                        color = BorderGrey,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onExpandedChange(true) },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = TealPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                option,
                                fontSize = 14.sp,
                                color = DarkGrey
                            ) 
                        },
                        onClick = {
                            onOptionSelected(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomDateField(
    label: String,
    date: LocalDate,
    onDateClick: () -> Unit
) {
    val formattedDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGrey,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Use a Box to ensure the clickable area covers the entire field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    Log.d("DateField", "Date field clicked")
                    onDateClick() 
                }
        ) {
            TextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Select Date",
                        tint = MediumGrey,
                        modifier = Modifier.clickable { 
                            Log.d("DateField", "Calendar icon clicked")
                            onDateClick() 
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .border(
                        width = 1.dp,
                        color = BorderGrey,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = TealPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}
