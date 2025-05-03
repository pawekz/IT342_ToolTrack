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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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

    var toolDetails by mutableStateOf<ToolBorrowItem?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var requestSuccess by mutableStateOf(false)
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

    fun requestBorrow(token: String, expectedReturnDate: String, notes: String? = null) {
        toolDetails?.let { tool ->
            viewModelScope.launch {
                isLoading = true
                errorMessage = null

                try {
                    val request = BorrowRequest(
                        tool_id = tool.tool_id,
                        expected_return_date = expectedReturnDate,
                        notes = notes
                    )

                    val response = borrowRequestApi.requestTool("Bearer $token", tool.tool_id, request)
                    if (response.isSuccessful) {
                        requestSuccess = true
                    } else {
                        errorMessage = "Failed to request tool: ${response.message()}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
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

    // Load tool details when the screen is first displayed
    LaunchedEffect(toolId) {
        viewModel.loadToolDetails(toolId, sessionManager)
    }

    // Handle navigation after successful request
    LaunchedEffect(viewModel.requestSuccess) {
        if (viewModel.requestSuccess) {
            Toast.makeText(context, "Tool requested successfully", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    // Handle error messages
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF2EA69E)
            )
        } else {
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
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Good condition button
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(15.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 4.dp
                                        ),
                                        onClick = { selectedCondition = true }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_good_condition),
                                                contentDescription = "Good Condition",
                                                tint = if (selectedCondition == true) Color(0xFF2EA69E) else Color.Black,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    // Bad condition button
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(15.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 4.dp
                                        ),
                                        onClick = { selectedCondition = false }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_bad_condition),
                                                contentDescription = "Bad Condition",
                                                tint = if (selectedCondition == false) Color.Black else Color.Black,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Request button
                                Button(
                                    onClick = {
                                        val token = sessionManager.fetchAuthToken()
                                        if (token != null) {
                                            viewModel.requestBorrow(
                                                token = token,
                                                expectedReturnDate = expectedReturnDate,
                                                notes = if (selectedCondition == false) "Tool in bad condition" else null
                                            )
                                        } else {
                                            Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2EA69E),
                                        contentColor = Color.White
                                    ),
                                    enabled = !viewModel.isLoading
                                ) {
                                    Text(
                                        text = "Request Item",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Good condition button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        onClick = { selectedCondition = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_good_condition),
                                contentDescription = "Good Condition",
                                tint = if (selectedCondition == true) Color(0xFF2EA69E) else Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    // Bad condition button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        onClick = { selectedCondition = false }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bad_condition),
                                contentDescription = "Bad Condition",
                                tint = if (selectedCondition == false) Color.Black else Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Request button
                Button(
                    onClick = { /* No-op in preview */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2EA69E),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Request Item",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
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

