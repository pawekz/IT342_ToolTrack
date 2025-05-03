package edu.cit.tooltrack.screens.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import edu.cit.tooltrack.LoginActivity
import edu.cit.tooltrack.R
import edu.cit.tooltrack.ui.theme.LightTeal
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import edu.cit.tooltrack.utils.SessionManager
import kotlinx.coroutines.launch
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
    val name: String,
    val imageUrl: String?,
    val borrowedDate: LocalDate,
    val returnDate: LocalDate,
    val status: String // "overdue", "active", "returned"
)

class BorrowedToolViewModel : ViewModel() {
    // Sample data
    private val _borrowedTools = mutableStateListOf(
        BorrowedTool(
            id = 1,
            name = "Electric Drill",
            imageUrl = null,
            borrowedDate = LocalDate.now().minusDays(5),
            returnDate = LocalDate.now().plusDays(2),
            status = "active"
        ),
        BorrowedTool(
            id = 2,
            name = "Circular Saw",
            imageUrl = null,
            borrowedDate = LocalDate.now().minusDays(10),
            returnDate = LocalDate.now().minusDays(2),
            status = "overdue"
        ),
        BorrowedTool(
            id = 3,
            name = "Hammer",
            imageUrl = null,
            borrowedDate = LocalDate.now().minusDays(15),
            returnDate = LocalDate.now().minusDays(8),
            status = "returned"
        ),
        BorrowedTool(
            id = 4,
            name = "Screwdriver Set",
            imageUrl = null,
            borrowedDate = LocalDate.now().minusDays(3),
            returnDate = LocalDate.now().plusDays(4),
            status = "active"
        ),
        BorrowedTool(
            id = 5,
            name = "Measuring Tape",
            imageUrl = null,
            borrowedDate = LocalDate.now().minusDays(8),
            returnDate = LocalDate.now().minusDays(1),
            status = "overdue"
        )
    )

    val borrowedTools: List<BorrowedTool> = _borrowedTools

    var searchQuery by mutableStateOf("")
        private set

    val filteredTools: List<BorrowedTool>
        get() = if (searchQuery.isEmpty()) {
            borrowedTools
        } else {
            borrowedTools.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowedToolScreen(
    viewModel: BorrowedToolViewModel = viewModel()
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

            // Tabs for filtering (Active, Overdue, Returned)
            BorrowedToolTabs()

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
fun BorrowedToolTabs() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "Overdue", "Returned")

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
                onClick = { selectedTabIndex = index },
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to tool details */ },
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
                                "active" -> Color(0xFF00E096)
                                "overdue" -> Color(0xFFFF3D71)
                                else -> Color(0xFFFFAA00)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (tool.status) {
                            "active" -> "Active"
                            "overdue" -> "Overdue"
                            else -> "Returned"
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
