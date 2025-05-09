package edu.cit.tooltrack.screens.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import edu.cit.tooltrack.R
import edu.cit.tooltrack.ui.theme.LightTeal
import edu.cit.tooltrack.ui.theme.ToolTrackTheme

class ToolDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get tool details from intent
        val toolId = intent.getIntExtra("TOOL_ID", 0)
        val toolName = intent.getStringExtra("TOOL_NAME") ?: ""
        val toolDescription = intent.getStringExtra("TOOL_DESCRIPTION") ?: ""
        val toolImageUrl = intent.getStringExtra("TOOL_IMAGE_URL") ?: ""
        val toolStatus = intent.getStringExtra("TOOL_STATUS") ?: ""
        val toolCategory = intent.getStringExtra("TOOL_CATEGORY") ?: ""

        setContent {
            ToolTrackTheme {
                ToolDetailsScreen(
                    toolId = toolId,
                    toolName = toolName,
                    toolDescription = toolDescription,
                    toolImageUrl = toolImageUrl,
                    toolStatus = toolStatus,
                    toolCategory = toolCategory,
                    onBackClick = { finish() }
                )
            }
        }
    }
}
@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailsScreen(
    toolId: Int = 0,
    toolName: String = "Sample Tool",
    toolDescription: String = "This is a sample tool description.",
    toolImageUrl: String = "",
    toolStatus: String = "available",
    toolCategory: String = "General",
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Surface(
                color = LightTeal,
                shadowElevation = 4.dp,
                /*shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)*/
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    // Back button (rounded rectangle)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                            .size(32.dp)
                            .background(
                                color = Color(0xB3E7F6F4),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onBackClick() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Centered title
                    Text(
                        text = "Tool Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Tool image - occupying 40% of the screen height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .weight(0.4f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(toolImageUrl)
                            .crossfade(true)
                            .error(R.drawable.ic_launcher_foreground)
                            .build()
                    ),
                    contentDescription = toolName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (toolStatus) {
                                "available" -> Color(0xFF00E096)
                                "in_use" -> Color(0xFFFF3D71)
                                else -> Color(0xFFFFAA00)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (toolStatus) {
                            "available" -> "Available"
                            "in_use" -> "In Use"
                            "borrowed" -> "Borrowed"
                            else -> "Maintenance"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Tool details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .padding(16.dp)
            ) {
                // Tool name
                Text(
                    text = toolName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3A59)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tool category
                Text(
                    text = "Category: $toolCategory",
                    fontSize = 16.sp,
                    color = Color(0xFF8F9BB3)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tool description
                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3A59)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = toolDescription,
                    fontSize = 16.sp,
                    color = Color(0xFF2E3A59),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tool ID
                Text(
                    text = "Tool ID: $toolId",
                    fontSize = 14.sp,
                    color = Color(0xFF8F9BB3)
                )
            }
        }
    }
}
