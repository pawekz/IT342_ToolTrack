package edu.cit.tooltrack.screens.scan

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import edu.cit.tooltrack.R
import edu.cit.tooltrack.api.ToolBorrowApi
import edu.cit.tooltrack.api.ToolBorrowItem
import edu.cit.tooltrack.api.ToolItem
import edu.cit.tooltrack.api.ToolTrackApi
import edu.cit.tooltrack.api.TransactionService
import edu.cit.tooltrack.api.TransactionRequest
import edu.cit.tooltrack.api.TransactionResponse
import edu.cit.tooltrack.api.Transaction
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import edu.cit.tooltrack.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Sample data for preview
private val sampleTool = ToolItem(
    id = 123,
    name = "Power Drill",
    description = "Heavy duty cordless power drill with variable speed settings. Perfect for drilling holes in wood, metal, and plastic materials. Comes with a set of drill bits and a carrying case.",
    status = "Available",
    imageUrl = "",
    categoryId = 1,
    categoryName = "Power Tools"
)

// Sample data for preview with tool condition and status
private val sampleToolBorrowItem = ToolBorrowItem(
    tool_id = 123,
    category = "Power Tools",
    name = "Power Drill",
    qr_code = "",
    location = "Tool Area A",
    description = "Heavy duty cordless power drill with variable speed settings. Perfect for drilling holes in wood, metal, and plastic materials. Comes with a set of drill bits and a carrying case.",
    date_acquired = "2025-05-01T00:00:00.000+00:00",
    image_url = "",
    created_at = "2025-05-03T05:49:40.505+00:00",
    updated_at = "2025-05-03T05:49:48.235+00:00",
    tool_condition = "GOOD",
    status = "AVAILABLE"
)

// Data classes for the borrow request
data class BorrowRequest(
    val tool_id: Int,
    val expected_return_date: String,
    val notes: String? = null
)

data class BorrowResponse(
    val id: Int,
    val tool_id: Int,
    val user_id: Int,
    val borrowed_date: String,
    val expected_return_date: String,
    val status: String
)

// API interface for borrow requests
interface BorrowRequestApi {
    @POST("borrows/request/{toolId}")
    suspend fun requestTool(
        @Header("Authorization") token: String,
        @Path("toolId") toolId: Int,
        @Body request: BorrowRequest
    ): Response<BorrowResponse>

    companion object {
        fun create(): BorrowRequestApi {
            return ToolTrackApi.retrofitInstance().create(BorrowRequestApi::class.java)
        }
    }
}

// ViewModel for the borrow request screen
class BorrowRequestViewModel : ViewModel() {
    private val toolBorrowApi = ToolBorrowApi.create()
    private val borrowRequestApi = BorrowRequestApi.create()
    private val transactionService = TransactionService.create()

    var toolDetails by mutableStateOf<ToolBorrowItem?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var requestSuccess by mutableStateOf(false)
        private set

    var transactionStatus by mutableStateOf<String?>(null)
        private set

    var transaction by mutableStateOf<Transaction?>(null)
        private set

    fun loadToolDetails(toolId: String, sessionManager: SessionManager) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val token = sessionManager.fetchAuthToken()
                if (token.isNullOrEmpty()) {
                    errorMessage = "Session expired. Please log in again."
                    isLoading = false
                    return@launch
                }

                val response = toolBorrowApi.getToolForBorrow(
                    token = "Bearer $token",
                    toolId = toolId.toInt()
                )

                if (response.isSuccessful) {
                    toolDetails = response.body()?.toolItem
                    if (toolDetails == null) {
                        errorMessage = "Tool details not found"
                    }
                } else {
                    errorMessage = "Failed to load tool details: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun requestBorrow(token: String, expectedReturnDate: String, notes: String? = null, email: String) {
        toolDetails?.let { tool ->
            viewModelScope.launch {
                isLoading = true
                errorMessage = null
                transactionStatus = null
                transaction = null

                try {
                    // First, make the original borrow request
                    val borrowRequest = BorrowRequest(
                        tool_id = tool.tool_id,
                        expected_return_date = expectedReturnDate,
                        notes = notes
                    )

                    val borrowResponse = borrowRequestApi.requestTool("Bearer $token", tool.tool_id, borrowRequest)

                    // Then, make the transaction request
                    val transactionRequest = TransactionRequest(
                        toolId = tool.tool_id,
                        email = email
                    )

                    val transactionResponse = transactionService.addTransaction(
                        token = "Bearer $token",
                        request = transactionRequest
                    )

                    if (transactionResponse.isSuccessful) {
                        transaction = transactionResponse.body()?.transaction
                        transactionStatus = transaction?.status
                        requestSuccess = true
                    } else {
                        errorMessage = "Failed to create transaction: ${transactionResponse.message()}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    fun refreshTransaction(token: String, transactionId: Int) {
        viewModelScope.launch {
            isRefreshing = true
            errorMessage = null

            try {
                val response = transactionService.getTransaction(
                    token = "Bearer $token",
                    transactionId = transactionId
                )

                if (response.isSuccessful) {
                    transaction = response.body()?.transaction
                    transactionStatus = transaction?.status
                } else {
                    errorMessage = "Failed to refresh transaction: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error refreshing: ${e.message}"
            } finally {
                isRefreshing = false
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BorrowRequestToolScreen(
    navController: NavHostController,
    toolId: String,
    viewModel: BorrowRequestViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var selectedCondition by remember { mutableStateOf<Boolean?>(null) }
    var expectedReturnDate by remember { mutableStateOf(
        LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_DATE)
    ) }

    // Add SnackbarHostState for showing messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Track if button should be disabled after request
    var isButtonDisabled by remember { mutableStateOf(false) }

    // Setup pull-to-refresh with debugging
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = {
            // Add a clear debug message if no transaction exists
            if (viewModel.transaction == null) {
                // You could show a Toast or SnackBar here
                // Toast.makeText(context, "No transaction to refresh", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.transaction?.let { transaction ->
                    val token = sessionManager.fetchAuthToken()
                    if (token != null) {
                        viewModel.refreshTransaction(token, transaction.transaction_id)
                    }
                }
            }
        }
    )

    // Load tool details when the screen is first displayed
    LaunchedEffect(toolId) {
        viewModel.loadToolDetails(toolId, sessionManager)
    }

    // Handle transaction status changes
    LaunchedEffect(viewModel.transactionStatus, viewModel.transaction) {
        viewModel.transaction?.let { transaction ->
            val transactionId = transaction.transaction_id
            val status = transaction.status.lowercase()

            isButtonDisabled = status != "available"

            when (status) {
                "pending" -> {
                    snackbarHostState.showSnackbar(
                        message = "Your transaction is pending with Transaction # $transactionId, go back later",
                        duration = SnackbarDuration.Long
                    )
                }
                "approved" -> {
                    snackbarHostState.showSnackbar(
                        message = "Your Transaction # $transactionId is approved, please get the tool",
                        duration = SnackbarDuration.Long
                    )
                }
                "declined" -> {
                    snackbarHostState.showSnackbar(
                        message = "Your transaction is declined with Transaction # $transactionId, please try later",
                        duration = SnackbarDuration.Long
                    )
                }
                "borrowed" -> {
                    // Already handled by disabling the button
                }
            }
        }
    }

    // Handle error messages
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }

    // Always enable pull-to-refresh
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF2EA69E)
            )
        } else {
            // Add SnackbarHost at the bottom of the screen
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                // Custom snackbar with light blue background for pending status
                snackbar = { data ->
                    val backgroundColor = when {
                        viewModel.transactionStatus?.lowercase() == "pending" -> Color(0xFF90CAF9) // Light blue color
                        viewModel.transactionStatus?.lowercase() == "declined" -> Color(0xFFF44336) // Red color
                        else -> Color(0xFF2EA69E) // Default teal color
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = data.visuals.message,
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    }
                }
            )
            viewModel.toolDetails?.let { tool ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Full screen layout with image at the top and card overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Tool image
                        Image(
                            painter = if (tool.image_url.isNotEmpty()) {
                                rememberAsyncImagePainter(tool.image_url)
                            } else {
                                painterResource(id = R.drawable.placeholder_tool)
                            },
                            contentDescription = tool.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(with(LocalDensity.current) {
                                    LocalConfiguration.current.screenHeightDp.dp * 0.45f
                                })
                        )

                        // Custom TopBar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Back button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .size(32.dp)
                                    .background(
                                        color = Color(0xB3E7F6F4),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { navController.navigateUp() }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Title
                            Text(
                                text = "Tool Details",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // Tool details card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(with(LocalDensity.current) {
                                    LocalConfiguration.current.screenHeightDp.dp * 0.6f
                                })
                                .align(Alignment.BottomCenter),
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 20.dp)
                            ) {
                                // Tool name and ID
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tool.name,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )

                                    Text(
                                        text = "Location: ${tool.location}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                }

                                // Category
                                Text(
                                    text = tool.category ?: "Uncategorized",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFFFC14D),
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Description section
                                Text(
                                    text = "Description",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )

                                Text(
                                    text = tool.description,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Condition section
                                Text(
                                    text = "Condition",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    // Display the tool condition with appropriate icon
                                    val conditionIcon = when (tool.tool_condition?.uppercase()) {
                                        "NEW" -> Icons.Filled.Star
                                        "GOOD" -> Icons.Filled.ThumbUp
                                        "FAIR" -> Icons.Filled.StarHalf
                                        "WORN" -> Icons.Filled.Cloud
                                        "DAMAGED" -> Icons.Filled.Warning
                                        "BROKEN" -> Icons.Filled.LinkOff
                                        else -> Icons.Filled.Star // Default to NEW if condition is null or unknown
                                    }

                                    val conditionColor = when (tool.tool_condition?.uppercase()) {
                                        "NEW" -> Color(0xFF4CAF50) // Green
                                        "GOOD" -> Color(0xFF8BC34A) // Light Green
                                        "FAIR" -> Color(0xFFFFC107) // Amber
                                        "WORN" -> Color(0xFFFF9800) // Orange
                                        "DAMAGED" -> Color(0xFFFF5722) // Deep Orange
                                        "BROKEN" -> Color(0xFFF44336) // Red
                                        else -> Color(0xFF4CAF50) // Default to Green if condition is null or unknown
                                    }

                                    Card(
                                        modifier = Modifier
                                            .padding(end = 16.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = conditionColor.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = conditionIcon,
                                                contentDescription = "Tool Condition",
                                                tint = conditionColor,
                                                modifier = Modifier.size(24.dp)
                                            )

                                            Text(
                                                text = tool.tool_condition ?: "NEW",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = conditionColor
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Check if the tool is available
                                val isToolAvailable = tool.status?.uppercase() == "AVAILABLE"

                                // Request button
                                Button(
                                    onClick = {
                                        if (!isToolAvailable) {
                                            // Show popup that the tool is currently borrowed
                                            Toast.makeText(context, "This tool is currently borrowed", Toast.LENGTH_SHORT).show()
                                        } else {
                                            val token = sessionManager.fetchAuthToken()
                                            if (token != null) {
                                                viewModel.requestBorrow(
                                                    token = token,
                                                    expectedReturnDate = expectedReturnDate,
                                                    notes = if (selectedCondition == false) "Tool in bad condition" else null,
                                                    email = sessionManager.getUserEmail()
                                                )
                                            } else {
                                                Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            isButtonDisabled -> Color.Gray
                                            isToolAvailable -> Color(0xFF2EA69E)
                                            else -> Color.Gray
                                        },
                                        contentColor = Color.White
                                    ),
                                    enabled = !viewModel.isLoading && isToolAvailable && !isButtonDisabled
                                ) {
                                    Text(
                                        text = "Request Tool",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Show message if tool is not available
                                if (!isToolAvailable) {
                                    Text(
                                        text = "This tool is currently borrowed",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } ?: run {
                // Show message if tool details are not available
                Text(
                    text = viewModel.errorMessage ?: "Loading tool details...",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // IMPORTANT: Make sure the PullRefreshIndicator is the LAST element in the Box
        // This ensures it appears on top of other content
        PullRefreshIndicator(
            refreshing = viewModel.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color(0xFF90CAF9),
            contentColor = Color(0xFF1976D2) // Darker blue for better contrast
        )
    }
}

// Preview version that doesn't depend on the ViewModel fetching data
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowRequestToolScreenWithSampleData(
    navController: NavHostController
) {
    val context = LocalContext.current
    var selectedCondition by remember { mutableStateOf<Boolean?>(null) }
    var expectedReturnDate by remember { mutableStateOf(
        LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_DATE)
    ) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tool image at the top
        Image(
            painter = painterResource(id = R.drawable.placeholder_tool),
            contentDescription = sampleTool.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) {
                    LocalConfiguration.current.screenHeightDp.dp * 0.45f
                })
        )

        // Custom TopBar with transparent background that overlays the image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Back button (rounded rectangle with arrow)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(32.dp)
                    .background(
                        color = Color(0xB3E7F6F4), // Transparent light teal
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { /* No-op in preview */ }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Title
            Text(
                text = "Tool Details",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Tool details card - positioned to partially overlay the image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) {
                    LocalConfiguration.current.screenHeightDp.dp * 0.6f
                })
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                // Tool name and ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sampleTool.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Text(
                        //it should be fetching the location
                        text = "ST#${sampleTool.id}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }

                // Availability
                Text(
                    text = "3 item/s left",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFC14D),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description section
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Text(
                    text = sampleTool.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Condition section
                Text(
                    text = "Condition",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Display the tool condition with appropriate icon
                    val conditionIcon = when (sampleToolBorrowItem.tool_condition?.uppercase()) {
                        "NEW" -> Icons.Filled.Star
                        "GOOD" -> Icons.Filled.ThumbUp
                        "FAIR" -> Icons.Filled.StarHalf
                        "WORN" -> Icons.Filled.Cloud
                        "DAMAGED" -> Icons.Filled.Warning
                        "BROKEN" -> Icons.Filled.LinkOff
                        else -> Icons.Filled.Star // Default to NEW if condition is null or unknown
                    }

                    val conditionColor = when (sampleToolBorrowItem.tool_condition?.uppercase()) {
                        "NEW" -> Color(0xFF4CAF50) // Green
                        "GOOD" -> Color(0xFF8BC34A) // Light Green
                        "FAIR" -> Color(0xFFFFC107) // Amber
                        "WORN" -> Color(0xFFFF9800) // Orange
                        "DAMAGED" -> Color(0xFFFF5722) // Deep Orange
                        "BROKEN" -> Color(0xFFF44336) // Red
                        else -> Color(0xFF4CAF50) // Default to Green if condition is null or unknown
                    }

                    Card(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = conditionColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = conditionIcon,
                                contentDescription = "Tool Condition",
                                tint = conditionColor,
                                modifier = Modifier.size(24.dp)
                            )

                            Text(
                                text = sampleToolBorrowItem.tool_condition ?: "NEW",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = conditionColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Check if the tool is available
                val isToolAvailable = sampleToolBorrowItem.status?.uppercase() == "AVAILABLE"

                // Request button
                Button(
                    onClick = { 
                        if (!isToolAvailable) {
                            // Show popup that the tool is currently borrowed
                            Toast.makeText(context, "This tool is currently borrowed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isToolAvailable) Color(0xFF2EA69E) else Color.Gray,
                        contentColor = Color.White
                    ),
                    enabled = isToolAvailable
                ) {
                    Text(
                        text = "Request Item",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Show message if tool is not available
                if (!isToolAvailable) {
                    Text(
                        text = "This tool is currently borrowed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBorrowRequestToolScreen() {
    ToolTrackTheme(dynamicColor = false, darkTheme = false) {
        val previewNavController = rememberNavController()
        BorrowRequestToolScreenWithSampleData(previewNavController)
    }
}