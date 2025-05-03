package edu.cit.tooltrack.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edu.cit.tooltrack.R
import edu.cit.tooltrack.ui.theme.LightTeal
import edu.cit.tooltrack.ui.theme.ToolTrackTheme

@Composable
fun AboutScreen(navController: NavHostController) {
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
            // Header
            AboutHeader(navController)
            
            // Content
            AboutContent()
        }
    }
}

@Composable
private fun AboutHeader(navController: NavHostController) {
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
                    .clickable { navController.popBackStack() }
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
                text = "About",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Add some padding at the bottom of the header
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AboutContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // About ToolTrack Section
        AboutSection(
            title = "About ToolTrack",
            description = "The ToolTrack: Smart Solutions for Seamless Tool Management is a user-friendly app that has been developed to track tool borrowing and return. Both the web and mobile parts make it easy for users to check on the availability of tools, borrowing history, and maintenance schedules."
        )
        
        // Developers Section
        Text(
            text = "Developers",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        // Developer Cards
        DeveloperCard(
            name = "Paulo Y. Carabuena",
            role = "Mobile Developer / Leader",
            imageResId = R.drawable.leadercat // Replace with actual image
        )
        
        DeveloperCard(
            name = "Aeron Clyde N. Espina",
            role = "Backend Developer",
            imageResId = R.drawable.backcat // Replace with actual image
        )
        
        DeveloperCard(
            name = "Nathaniel Salvoro",
            role = "Frontend Developer",
            imageResId = R.drawable.frontcat // Replace with actual image
        )
    }
}

@Composable
private fun AboutSection(title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        
        Text(
            text = description,
            fontSize = 12.sp,
            color = Color.Black,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun DeveloperCard(name: String, role: String, imageResId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xB3E7F6F4)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Developer Image
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Developer Image",
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Developer Info
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            Text(
                text = role,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    ToolTrackTheme(dynamicColor = false, darkTheme = false) {
        Surface(color = Color.White) {
            AboutScreen(rememberNavController())
        }
    }
}