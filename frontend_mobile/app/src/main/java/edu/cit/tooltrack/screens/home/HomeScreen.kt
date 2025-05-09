// screens/home/HomeScreen.kt
package edu.cit.tooltrack.screens.home

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import edu.cit.tooltrack.LoginActivity
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
    val context = LocalContext.current
    val appContext = context.applicationContext as Application
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(appContext))
    val isLoading = viewModel.isLoading
    val categories = viewModel.categories
    val isCategoriesExpanded = viewModel.isCategoriesExpanded
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Logic for category focus display
        if (viewModel.selectedCategory != null) {
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isCategoryLoading)

            // Display tools in selected category with a swipe-refresh
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar with back button and category name - updated to match AboutScreen style
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = LightTeal,
                            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                        )
                ) {
                    // Title with back button
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
                                .clickable { viewModel.clearCategorySelection() }
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
                            text = viewModel.selectedCategory?.name ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Add search bar inside the header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Reuse the SearchBar component but for category filtering
                        CategorySearchBar(
                            query = viewModel.categorySearchQuery,
                            onQueryChange = { viewModel.updateCategorySearchQuery(it) },
                            placeholder = "Search in ${viewModel.selectedCategory?.name}..."
                        )
                    }

                    // Add some padding at the bottom of the header
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Swipe-to-refresh layout - add padding to match Popular Tools section
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.loadCategoryTools(viewModel.selectedCategory!!, forceRefresh = true)
                    }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ToolsSection(
                            tools = viewModel.categoryTools,
                            title = "${viewModel.selectedCategory?.name} Tools",
                            isLoading = viewModel.isCategoryLoading,
                            errorMessage = viewModel.categoryErrorMessage,
                            onToolClick = { tool ->
                                val intent = Intent(context, ToolDetailsActivity::class.java).apply {
                                    putExtra("TOOL_ID", tool.id)
                                    putExtra("TOOL_NAME", tool.name)
                                    putExtra("TOOL_DESCRIPTION", tool.description)
                                    putExtra("TOOL_IMAGE_URL", tool.imageUrl)
                                    putExtra("TOOL_STATUS", tool.status)
                                    putExtra("TOOL_CATEGORY", tool.categoryName)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        } else {
            // Default HomeScreen content when no category is selected
            Column(modifier = Modifier.fillMaxSize()) {
                HomeHeader(
                    searchQuery = viewModel.searchQuery,
                    onQueryChange = { query ->
                        viewModel.updateSearchQuery(query)
                        // No need to call searchToolByName here as performSearch will handle it
                        // if the search endpoint returns a 403 Forbidden response
                    },
                    isLoading = isLoading,
                    onSearch = { query ->
                        if (query.isNotBlank()) {
                            viewModel.updateSearchQuery(query)
                            // No need to call searchToolByName here as performSearch will handle it
                            // if the search endpoint returns a 403 Forbidden response
                        } else {
                            viewModel.clearSearchResult()
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    CategorySection(
                        categories = categories,
                        onCategoryClick = { category ->
                            viewModel.selectCategory(category) // Update to select category
                        },
                        isExpanded = isCategoriesExpanded,
                        onToggleExpanded = { viewModel.toggleCategoriesExpanded() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ToolsSection(
                        tools = viewModel.searchResults,
                        title = if (viewModel.searchQuery.isNotEmpty()) "Search Results" else "Popular Tools",
                        isLoading = isLoading,
                        errorMessage = viewModel.errorMessage,
                        onToolClick = { tool ->
                            // Navigate to ToolDetailsActivity
                            val intent = Intent(context, ToolDetailsActivity::class.java).apply {
                                putExtra("TOOL_ID", tool.id)
                                putExtra("TOOL_NAME", tool.name)
                                putExtra("TOOL_DESCRIPTION", tool.description)
                                putExtra("TOOL_IMAGE_URL", tool.imageUrl)
                                putExtra("TOOL_STATUS", tool.status)
                                putExtra("TOOL_CATEGORY", tool.categoryName)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp)),
        placeholder = { Text(placeholder) },
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean,
    onSearch: (String) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn = sessionManager.isLoggedIn()
    val firstName = if (isLoggedIn) sessionManager.getUserFirstName() else ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LightTeal,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier) {}
            IconButton(onClick = { /* TODO: Go to notifications */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF2E3A59)
                )
            }
        }
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
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            isLoading = isLoading,
            onSearch = onSearch
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean,
    onSearch: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
            if (it.isNotBlank()) {
                onSearch(it)
            } else {
                onSearch("")
            }
        },
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

    Spacer(modifier = Modifier.height(16.dp))
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )

        {
            Text(
                text = "Tool Categories",
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
    // Fixed size for both width and height to ensure consistent square appearance
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .size(80.dp), // Fixed square size for consistency
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Using Material icons based on category name
                when (category.name) {
                    "Power" -> Icon(
                        painter = painterResource(id = R.drawable.tools_power_drill_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Hand" -> Icon(
                        painter = painterResource(id = R.drawable.handyman_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Garden" -> Icon(
                        painter = painterResource(id = R.drawable.yard_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Electrical" -> Icon(
                        painter = painterResource(id = R.drawable.bolt_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Plumbing" -> Icon(
                        painter = painterResource(id = R.drawable.faucet_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Painting" -> Icon(
                        painter = painterResource(id = R.drawable.format_paint_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Automotive" -> Icon(
                        painter = painterResource(id = R.drawable.directions_car_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Measuring" -> Icon(
                        painter = painterResource(id = R.drawable.measuring_tape_24),
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF2EA69E)
                    )
                    "Safety" -> Icon(
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
    errorMessage: String?,
    onToolClick: (ToolItem) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Title is outside the scrollable area
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E3A59),
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )

        if (isLoading) {
            // Show skeleton loading
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) {
                    ToolItemSkeleton()
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
            // Use fixed-height items with pre-rendered heights for better performance
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(), 
                contentPadding = PaddingValues(bottom = 32.dp), // Increased bottom padding for last item
                // Use fixed height hint for items if they're roughly the same height
                // This significantly improves performance
                flingBehavior = ScrollableDefaults.flingBehavior()
            ) {
                items(
                    items = tools,
                    // Use unique ID as key for stable item identity
                    key = { it.id }
                ) { tool ->
                    // Add Box wrapper with fixed height for more predictable rendering
                    Box(modifier = Modifier.height(130.dp)) {
                        ToolItem(
                            tool = tool,
                            onClick = { onToolClick(tool) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolItemSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image skeleton
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp) // Fixed width for consistency
                    .padding(4.dp) // Add padding around the image
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .padding(start = 8.dp) // Extra padding on the right side of the image
            ) {
                // Title skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(16.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status skeleton
                Row {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(24.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(24.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun ToolItem(tool: ToolItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), // Added for consistent height
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fixed square box for image with consistent dimensions
            Box(
                modifier = Modifier
                    .size(100.dp) // Fixed square size for consistency
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // Pre-calculate image size for better performance
                val imageSize = 100.dp
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(tool.imageUrl)
                            .crossfade(true)
                            .size(width = 100, height = 100) // Specify size for memory efficiency
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .memoryCacheKey(tool.imageUrl) // Add cache key for better caching
                            .build()
                    ),
                    contentDescription = tool.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .padding(start = 8.dp)
            ) {
                // Title text - no changes needed
                Text(
                    text = tool.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3A59)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Optimized description text handling
                // Pre-process the description to avoid layout calculation issues
                val processedDescription = remember(tool.description) {
                    if (tool.description.length > 70) {
                        tool.description.take(70) + "..."
                    } else {
                        tool.description
                    }
                }

                // Use BasicText for better performance with fixed height
                BasicText(
                    text = processedDescription,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF8F9BB3),
                        // Ensure consistent height by setting lineHeight
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.height(32.dp) // Fixed height for 2 lines
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (tool.status.lowercase()) {
                                    "available" -> Color(0xFF00E096)
                                    "in_use" -> Color(0xFFFF3D71)
                                    else -> Color(0xFFFFAA00)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (tool.status.lowercase()) {
                                "available" -> "Available"
                                "in_use" -> "In Use"
                                "borrowed" -> "Borrowed"
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
        ToolCategory(4, "Electrical", "Tools for electrical work", ""),
        ToolCategory(5, "Plumbing", "Tools for plumbing tasks", ""),
        ToolCategory(6, "Painting", "Tools for painting projects", ""),
        ToolCategory(7, "Automotive", "Tools for vehicle maintenance", ""),
        ToolCategory(8, "Measuring", "Tools for precise measurements", ""),
        ToolCategory(9, "Safety", "Personal protective equipment", "")
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
        ) {
            // Header with green background
            HomeHeader(
                searchQuery = "",
                onQueryChange = { },
                isLoading = false,
                onSearch = { }
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
