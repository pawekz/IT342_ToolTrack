package edu.cit.tooltrack.screens.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import edu.cit.tooltrack.screens.home.ToolDetailsActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import edu.cit.tooltrack.LoginActivity
import edu.cit.tooltrack.R
import edu.cit.tooltrack.api.MyTransactionItem
import edu.cit.tooltrack.api.MyTransactionsResponse
import edu.cit.tooltrack.api.ToolBorrowApi
import edu.cit.tooltrack.api.TransactionService
import edu.cit.tooltrack.ui.theme.LightTeal
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import edu.cit.tooltrack.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BorrowedToolActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToolTrackTheme {
                BorrowedToolScreen()
            }
        }
    }
}

data class BorrowedTool(
    val id: Int,
    val tool_id: Int, // Added tool_id field
    val name: String,
    val imageUrl: String?,
    val description: String,
    val category: String,
    val borrowedDate: LocalDate,
    val returnDate: LocalDate,
    val status: String // "overdue", "active", "returned"
)

class BorrowedToolViewModel(private val sessionManager: SessionManager) : ViewModel() {
    // Mutable state for borrowed tools
    private val _borrowedTools = mutableStateListOf<BorrowedTool>()

    // Loading state
    private var _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Error state
    private var _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // Selected tab state (0 = Borrowed, 1 = Overdue, 2 = All)
    private var _selectedTab = mutableStateOf(0)
    val selectedTab: State<Int> = _selectedTab

    // Original transactions from API
    private var _originalTransactions = mutableStateListOf<MyTransactionItem>()

    // Tool borrow API for fetching tool details
    private val toolBorrowApi = ToolBorrowApi.create()

    val borrowedTools: List<BorrowedTool> = _borrowedTools

    var searchQuery by mutableStateOf("")
        private set

    // Update selected tab
    fun updateSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
        // Reapply filtering based on the new tab
        applyFilters()
    }

    val filteredTools: List<BorrowedTool>
        get() {
            // First filter by search query
            val searchFiltered = if (searchQuery.isEmpty()) {
                borrowedTools
            } else {
                borrowedTools.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            // Then filter by selected tab
            return when (_selectedTab.value) {
                0 -> searchFiltered.filter { it.status == "active" } // Borrowed tab
                1 -> searchFiltered.filter { it.status == "overdue" } // Overdue tab
                2 -> searchFiltered.sortedByDescending { it.returnDate } // All tab, sorted by due date (latest first)
                else -> searchFiltered
            }
        }

    init {
        // Fetch user transactions when ViewModel is created
        fetchUserTransactions()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    // Function to apply filters based on the selected tab
    private fun applyFilters() {
        viewModelScope.launch {
            _borrowedTools.clear()

            // Process original transactions based on selected tab
            val filteredTransactions = _originalTransactions.map { transaction ->
                // Parse dates from strings to LocalDate
                val borrowDate = try {
                    if (transaction.borrow_date.isNotEmpty()) {
                        // Parse ISO date format (adjust if needed)
                        LocalDate.parse(transaction.borrow_date.split("T")[0])
                    } else {
                        LocalDate.now()
                    }
                } catch (e: Exception) {
                    Log.e("BorrowedToolViewModel", "Error parsing borrow date: ${e.message}")
                    LocalDate.now()
                }

                // Parse due date with UTC+8 conversion for overdue calculation
                val dueDate = try {
                    if (!transaction.due_date.isNullOrEmpty()) {
                        // Parse ISO date format and add 8 hours for UTC+8
                        val dateStr = transaction.due_date.split("T")[0]
                        val timeStr = transaction.due_date.split("T")[1].split(".")[0]
                        val hours = timeStr.split(":")[0].toInt()
                        val minutes = timeStr.split(":")[1].toInt()
                        val seconds = timeStr.split(":")[2].toInt()

                        // Convert to UTC+8 by adding 8 hours
                        val adjustedHours = (hours + 8) % 24
                        val nextDay = (hours + 8) >= 24

                        val dueDateBase = LocalDate.parse(dateStr)
                        // If crossing to next day, add 1 day to the date
                        val adjustedDate = if (nextDay) dueDateBase.plusDays(1) else dueDateBase
                        adjustedDate
                    } else {
                        borrowDate.plusDays(7) // Default to 7 days if no due date
                    }
                } catch (e: Exception) {
                    Log.e("BorrowedToolViewModel", "Error parsing due date: ${e.message}")
                    borrowDate.plusDays(7)
                }

                // Map transaction status to BorrowedTool status based on the selected tab
                val toolStatus = when (_selectedTab.value) {
                    0 -> { // Borrowed tab - show approved transactions
                        if (transaction.status == "approved" && transaction.return_date == null) "active" else transaction.status
                    }
                    1 -> { // Overdue tab - check if due date is past
                        if (transaction.status == "approved" && transaction.return_date == null && 
                            LocalDate.now().isAfter(dueDate)) "overdue" else transaction.status
                    }
                    2 -> { // All tab - show all transactions with their status
                        when {
                            transaction.status == "approved" && transaction.return_date == null -> {
                                if (LocalDate.now().isAfter(dueDate)) "overdue" else "active"
                            }
                            transaction.status == "pending" -> "pending"
                            transaction.status == "rejected" -> "rejected"
                            transaction.return_date != null -> "returned"
                            else -> transaction.status
                        }
                    }
                    else -> transaction.status
                }

                // Fetch tool details to get the image URL
                try {
                    // Get token from SessionManager
                    val token = sessionManager.fetchAuthToken()
                    if (!token.isNullOrEmpty()) {
                        // Call the API to get tool details
                        val toolResponse = toolBorrowApi.getToolForBorrow("Bearer $token", transaction.tool_id)

                        if (toolResponse.isSuccessful && toolResponse.body() != null) {
                            val toolItem = toolResponse.body()!!.toolItem

                            BorrowedTool(
                                id = transaction.transaction_id,
                                tool_id = transaction.tool_id,
                                name = transaction.tool_name,
                                imageUrl = toolItem.image_url, // Use image URL from tool details
                                description = toolItem.description,
                                category = toolItem.category ?: "General",
                                borrowedDate = borrowDate,
                                returnDate = dueDate,
                                status = toolStatus
                            )
                        } else {
                            // If API call fails, create BorrowedTool without image URL
                            Log.e("BorrowedToolViewModel", "Failed to fetch tool details: ${toolResponse.message()}")
                            BorrowedTool(
                                id = transaction.transaction_id,
                                tool_id = transaction.tool_id,
                                name = transaction.tool_name,
                                imageUrl = null,
                                description = "No description available",
                                category = "General",
                                borrowedDate = borrowDate,
                                returnDate = dueDate,
                                status = toolStatus
                            )
                        }
                    } else {
                        // If token is null or empty, create BorrowedTool without image URL
                        BorrowedTool(
                            id = transaction.transaction_id,
                            tool_id = transaction.tool_id,
                            name = transaction.tool_name,
                            imageUrl = null,
                            description = "No description available",
                            category = "General",
                            borrowedDate = borrowDate,
                            returnDate = dueDate,
                            status = toolStatus
                        )
                    }
                } catch (e: Exception) {
                    // If an exception occurs, create BorrowedTool without image URL
                    Log.e("BorrowedToolViewModel", "Exception fetching tool details: ${e.message}", e)
                    BorrowedTool(
                        id = transaction.transaction_id,
                        tool_id = transaction.tool_id,
                        name = transaction.tool_name,
                        imageUrl = null,
                        description = "No description available",
                        category = "General",
                        borrowedDate = borrowDate,
                        returnDate = dueDate,
                        status = toolStatus
                    )
                }
            }

            // Filter and sort based on selected tab
            val finalList = when (_selectedTab.value) {
                0 -> filteredTransactions.filter { it.status == "active" } // Borrowed tab
                1 -> filteredTransactions.filter { it.status == "overdue" } // Overdue tab
                2 -> filteredTransactions.sortedByDescending { it.returnDate } // All tab, sorted by due date (latest first)
                else -> filteredTransactions
            }

            _borrowedTools.addAll(finalList)
            Log.d("BorrowedToolViewModel", "Applied filters for tab ${_selectedTab.value}, found ${finalList.size} tools")
        }
    }

    fun fetchUserTransactions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Get user email from SessionManager
                val userEmail = sessionManager.getUserEmail()
                if (userEmail.isEmpty()) {
                    _error.value = "User email not found"
                    return@launch
                }

                // Get token from SessionManager
                val token = sessionManager.fetchAuthToken()
                if (token.isNullOrEmpty()) {
                    _error.value = "Authentication token not found"
                    return@launch
                }

                // Create TransactionService instance
                val transactionService = TransactionService.create()

                // Call the API to get user transactions
                val response = transactionService.getMyTransactions("Bearer $token", userEmail)

                if (response.isSuccessful && response.body() != null) {
                    // Store original transactions
                    _originalTransactions.clear()
                    _originalTransactions.addAll(response.body()!!.transactions)

                    // Apply filters based on the current tab
                    applyFilters()

                    Log.d("BorrowedToolViewModel", "Fetched ${_originalTransactions.size} transactions")
                } else {
                    // Handle error
                    _error.value = "Failed to fetch transactions: ${response.message()}"
                    Log.e("BorrowedToolViewModel", "API error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                _error.value = "Error: ${e.message}"
                Log.e("BorrowedToolViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Factory for creating BorrowedToolViewModel with the required SessionManager dependency
 */
class BorrowedToolViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BorrowedToolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BorrowedToolViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowedToolScreen(
    viewModel: BorrowedToolViewModel = viewModel(
        factory = BorrowedToolViewModelFactory(SessionManager(LocalContext.current))
    )
) {
    val filteredTools = viewModel.filteredTools
    val searchQuery = viewModel.searchQuery

    // Get the current context and session manager
    val context = LocalContext.current
    val activity = LocalActivity.current
    val sessionManager = remember { SessionManager(context) }

    // State for showing the snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Check if token is expired
    if (sessionManager.isTokenExpired()) {
        // Show snackbar and navigate to login
        LaunchedEffect(key1 = true) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Your session has expired. Please log in again.",
                    duration = SnackbarDuration.Short
                )
                // Navigate to login screen after showing snackbar
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                activity?.finish()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp)
        ) {
            // Header
            BorrowedToolHeader(
                searchQuery = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) }
            )

            // Tabs for filtering (Borrowed, Overdue, All)
            BorrowedToolTabs(viewModel)

            // List of borrowed tools
            BorrowedToolList(tools = filteredTools)
        }

        // Snackbar host for showing messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowedToolHeader(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LightTeal,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        // Title with back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            BackButton(context)

            // Title
            Text(
                text = "Borrowed Tools",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp)),
            placeholder = { Text("Search for tools...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF8F9BB3)
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF3366FF)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun BackButton(context: Context) {
    // Back button
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(
                color = Color(0xB3E7F6F4), // Transparent light teal
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { 
                // This will close the current activity and return to the previous one (ProfileScreen)
                (context as? Activity)?.finish() 
            }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun BorrowedToolTabs(viewModel: BorrowedToolViewModel) {
    val selectedTabIndex = viewModel.selectedTab.value
    val tabs = listOf("Borrowed", "Overdue", "All")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = Color(0xFF3366FF),
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(3.dp)
                    .padding(horizontal = 40.dp)
                    .background(
                        color = Color(0xFF3366FF),
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                    )
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { viewModel.updateSelectedTab(index) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                },
                selectedContentColor = Color(0xFF3366FF),
                unselectedContentColor = Color(0xFF8F9BB3)
            )
        }
    }

    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
}

@Composable
fun BorrowedToolList(tools: List<BorrowedTool>) {
    if (tools.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No borrowed tools found",
                color = Color(0xFF8F9BB3)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(tools) { tool ->
                BorrowedToolItem(tool = tool)
            }
        }
    }
}

@Composable
fun BorrowedToolItem(tool: BorrowedTool) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                // Navigate to tool details
                val intent = Intent(context, ToolDetailsActivity::class.java)
                intent.putExtra("TOOL_ID", tool.tool_id)
                intent.putExtra("TOOL_NAME", tool.name)
                intent.putExtra("TOOL_DESCRIPTION", tool.description)
                intent.putExtra("TOOL_IMAGE_URL", tool.imageUrl ?: "")
                // Map our status to what ToolDetailsActivity expects
                val mappedStatus = when (tool.status) {
                    "active" -> "borrowed"
                    "overdue" -> "in_use"
                    "pending" -> "available"
                    "rejected" -> "available"
                    "returned" -> "available"
                    else -> "available"
                }
                intent.putExtra("TOOL_STATUS", mappedStatus)
                intent.putExtra("TOOL_CATEGORY", tool.category)
                // Add borrow and due date information
                intent.putExtra("TOOL_BORROW_DATE", tool.borrowedDate.format(dateFormatter))
                intent.putExtra("TOOL_DUE_DATE", tool.returnDate.format(dateFormatter))
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tool image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                if (tool.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(tool.imageUrl)
                                .crossfade(true)
                                .error(R.drawable.ic_launcher_foreground)
                                .build()
                        ),
                        contentDescription = tool.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Default tool icon
                    Icon(
                        painter = painterResource(id = R.drawable.handyman_24),
                        contentDescription = tool.name,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center),
                        tint = Color(0xFF2EA69E)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tool info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Tool name
                Text(
                    text = tool.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (tool.status) {
                                "active" -> Color(0xFF00E096)  // Green for active/borrowed
                                "overdue" -> Color(0xFFFF3D71) // Red for overdue
                                "pending" -> Color(0xFF3366FF) // Blue for pending
                                "rejected" -> Color(0xFFFF6B4A) // Orange-red for rejected
                                "returned" -> Color(0xFF8F9BB3) // Gray for returned
                                else -> Color(0xFFFFAA00)      // Yellow for other statuses
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (tool.status) {
                            "active" -> "Borrowed"
                            "overdue" -> "Overdue"
                            "pending" -> "Pending"
                            "rejected" -> "Rejected"
                            "returned" -> "Returned"
                            else -> tool.status.replaceFirstChar { it.uppercase() }
                        },
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dates
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF8F9BB3)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tool.borrowedDate.format(dateFormatter)} - ${tool.returnDate.format(dateFormatter)}",
                        fontSize = 12.sp,
                        color = Color(0xFF8F9BB3)
                    )
                }
            }

            // Arrow icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View Details",
                tint = Color(0xFF8F9BB3)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BorrowedToolScreenPreview() {
    ToolTrackTheme {
        BorrowedToolScreen()
    }
}
