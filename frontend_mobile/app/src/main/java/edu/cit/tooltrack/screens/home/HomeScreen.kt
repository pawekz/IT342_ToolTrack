// screens/home/HomeScreen.kt
package edu.cit.tooltrack.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import edu.cit.tooltrack.R
import edu.cit.tooltrack.api.ToolCategory
import edu.cit.tooltrack.api.ToolItem
import edu.cit.tooltrack.ui.theme.LightTeal
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import edu.cit.tooltrack.utils.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = viewModel()
    val searchQuery = viewModel.searchQuery
    val searchResults = viewModel.searchResults
    val categories = viewModel.categories
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val isCategoriesExpanded = viewModel.isCategoriesExpanded

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Add padding for bottom navigation
        ) {
            // Header with green background
            HomeHeader(
                searchQuery = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                isLoading = isLoading
            )

            // Main content with categories and tools
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Categories - Display without triggering search
                if (categories.isNotEmpty()) {
                    CategorySection(
                        categories = categories,
                        onCategoryClick = { /* Do not call search */ },
                        isExpanded = isCategoriesExpanded,
                        onToggleExpanded = { viewModel.toggleCategoriesExpanded() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search Results or Popular Tools
                ToolsSection(
                    tools = searchResults,
                    title = if (searchQuery.isNotEmpty()) "Search Results" else "Popular Tools",
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean
) {
    // Get the current context to initialize SessionManager
    val context = LocalContext.current
    // Initialize SessionManager to access user data
    val sessionManager = remember { SessionManager(context) }

    // Check if user is logged in (token is valid and not expired)
    val isLoggedIn = sessionManager.isLoggedIn()

    // Get the user's first name from SessionManager only if logged in
    val firstName = if (isLoggedIn) sessionManager.getUserFirstName() else ""

     Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LightTeal, // Teal background to match ProfileScreen
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        // Top Bar with profile and notifications
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Empty box on the left (placeholder for profile icon)
            Box(modifier = Modifier) {
                // Empty space where profile icon used to be
            }

            // Notification icon on the right
            IconButton(onClick = { /* TODO: Go to notifications */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF2E3A59)
                )
            }
        }

        /*Spacer(modifier = Modifier.height(16.dp))*/

        // Greeting with personalized first name
        Text(
            text = if (firstName.isNotEmpty()) "Hello there $firstName!" else "Hello, there!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E3A59)
        )

        Text(
            text = "Find your tools",
            fontSize = 16.sp,
            color = Color(0xFF8F9BB3),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean
) {
    TextField(
        value = query,
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
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
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
}

@Composable
fun CategorySection(
    categories: List<ToolCategory>,
    onCategoryClick: (ToolCategory) -> Unit,
    isExpanded: Boolean = false,
    onToggleExpanded: () -> Unit = {}
) {
    // Sample extended categories - in a real app, these would come from your data source
    val extendedCategories = rememberExtendedCategories(categories)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )

            Text(
                text = if (isExpanded) "Show Less" else "See All",
                fontSize = 14.sp,
                color = Color(0xFF3366FF),
                modifier = Modifier.clickable { onToggleExpanded() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isExpanded) {
            // Grid layout for expanded view
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(extendedCategories) { category ->
                    CategoryItem(
                        category = category,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }

            // Button to collapse back
            Button(
                onClick = { onToggleExpanded() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3366FF)
                )
            ) {
                Text("Show Less", color = Color.White)
            }
        } else {
            // Carousel implementation for collapsed view
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp) // Fixed height for the carousel
            ) {
                val scrollState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()

                LazyRow(
                    state = scrollState,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(extendedCategories) { category ->
                        CategoryItem(category = category, onClick = { onCategoryClick(category) })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                // Add indicators and navigation arrows if desired
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    extendedCategories.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp)
                                .background(
                                    color = if (
                                        // Check if this index is visible in the viewport
                                        index >= scrollState.firstVisibleItemIndex && 
                                        index <= scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size - 1
                                    ) Color(0xFF3366FF) else Color(0xFFD9E3F0),
                                    shape = CircleShape
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        scrollState.animateScrollToItem(index)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberExtendedCategories(originalCategories: List<ToolCategory>): List<ToolCategory> {
    // Simply return the original categories as we now have a complete list
    return remember(originalCategories) {
        originalCategories
    }
}

@Composable
fun CategoryItem(
    category: ToolCategory,
    onClick: () -> Unit
) {
    // Enhanced version with a nice shadow and cleaner design
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(80.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White) // Solid white background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Using Material icons based on category name
                when (category.name) {
                    "Power Tools" -> Icon(
                        painter = painterResource(id = R.drawable.tools_power_drill_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Hand Tools" -> Icon(
                        painter = painterResource(id = R.drawable.handyman_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Garden Tools" -> Icon(
                        painter = painterResource(id = R.drawable.yard_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Electrical Tools" -> Icon(
                        painter = painterResource(id = R.drawable.bolt_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Plumbing Tools" -> Icon(
                        painter = painterResource(id = R.drawable.faucet_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Painting Tools" -> Icon(
                        painter = painterResource(id = R.drawable.format_paint_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Automotive Tools" -> Icon(
                        painter = painterResource(id = R.drawable.directions_car_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Measuring Tools" -> Icon(
                        painter = painterResource(id = R.drawable.measuring_tape_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Safety Equipment" -> Icon(
                        painter = painterResource(id = R.drawable.masks_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    else -> Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(category.imageUrl)
                                .crossfade(true)
                                .error(R.drawable.ic_launcher_foreground)
                                .build()
                        ),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name,
            fontSize = 14.sp,
            color = Color(0xFF2E3A59),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
@Composable
fun ToolsSection(
    tools: List<ToolItem>,
    title: String,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E3A59),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF3366FF))
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        } else if (tools.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (title == "Search Results") "No results found" else "No tools available",
                    color = Color(0xFF8F9BB3),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tools) { tool ->
                    ToolItem(tool = tool)
                }
            }
        }
    }
}

@Composable
fun ToolItem(tool: ToolItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navigate to detail screen */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Using rememberAsyncImagePainter instead of AsyncImage for Coil 2.4.0
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(tool.imageUrl)
                        .crossfade(true)
                        .error(R.drawable.ic_launcher_foreground)
                        .build()
                ),
                contentDescription = tool.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3A59)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tool.description,
                    fontSize = 14.sp,
                    color = Color(0xFF8F9BB3),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (tool.status) {
                                    "available" -> Color(0xFF00E096)
                                    "in_use" -> Color(0xFFFF3D71)
                                    else -> Color(0xFFFFAA00)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (tool.status) {
                                "available" -> "Available"
                                "in_use" -> "In Use"
                                else -> "Maintenance"
                            },
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = tool.categoryName,
                        fontSize = 12.sp,
                        color = Color(0xFF8F9BB3)
                    )
                }
            }
        }
    }
}

// Sample data for preview
class SampleToolCategoryProvider : PreviewParameterProvider<List<ToolCategory>> {
    override val values = sequenceOf(
        listOf(
            ToolCategory(1, "Power Tools", "Various power tools", ""),
            ToolCategory(2, "Hand Tools", "Manual hand tools", ""),
            ToolCategory(3, "Garden Tools", "Tools for gardening", "")
        )
    )
}

class SampleToolItemProvider : PreviewParameterProvider<List<ToolItem>> {
    override val values = sequenceOf(
        listOf(
            ToolItem(1, "Electric Drill", "Powerful electric drill for any drilling task", "", "available", 1, "Power Tools"),
            ToolItem(2, "Hammer", "Standard claw hammer for general use", "", "in_use", 2, "Hand Tools"),
            ToolItem(3, "Lawn Mower", "Gas-powered lawn mower for large yards", "", "maintenance", 3, "Garden Tools")
        )
    )
}

@Preview(name = "Home Screen", showBackground = true, showSystemUi = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    ToolTrackTheme(dynamicColor = false, darkTheme = false) {
        Surface(color = Color.White) {
            HomePreviewContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomePreviewContent() {
    val sampleCategories = listOf(
        ToolCategory(1, "Power Tools", "Various power tools", ""),
        ToolCategory(2, "Hand Tools", "Manual hand tools", ""),
        ToolCategory(3, "Garden Tools", "Tools for gardening", ""),
        ToolCategory(4, "Electrical Tools", "Tools for electrical work", ""),
        ToolCategory(5, "Plumbing Tools", "Tools for plumbing tasks", ""),
        ToolCategory(6, "Painting Tools", "Tools for painting projects", ""),
        ToolCategory(7, "Automotive Tools", "Tools for vehicle maintenance", ""),
        ToolCategory(8, "Measuring Tools", "Tools for precise measurements", ""),
        ToolCategory(9, "Safety Equipment", "Personal protective equipment", "")
    )

    val sampleTools = listOf(
        ToolItem(1, "Electric Drill", "Powerful electric drill for any drilling task", "", "available", 1, "Power Tools"),
        ToolItem(2, "Hammer", "Standard claw hammer for general use", "", "in_use", 2, "Hand Tools"),
        ToolItem(3, "Lawn Mower", "Gas-powered lawn mower for large yards", "", "maintenance", 3, "Garden Tools"),
        ToolItem(4, "Screwdriver Set", "Complete set of Phillips and flathead screwdrivers", "", "available", 2, "Hand Tools"),
        ToolItem(5, "Circular Saw", "Precision cutting for woodworking projects", "", "in_use", 1, "Power Tools")
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Add padding for bottom navigation
        ) {
            // Header with green background
            HomeHeader(
                searchQuery = "",
                onQueryChange = { },
                isLoading = false
            )

            // Main content with categories and tools
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Categories
                CategorySection(
                    categories = sampleCategories,
                    onCategoryClick = { },
                    isExpanded = false,
                    onToggleExpanded = { }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Search Results or Popular Tools
                ToolsSection(
                    tools = sampleTools,
                    title = "Popular Tools",
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }
}
