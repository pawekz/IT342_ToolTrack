package edu.cit.tooltrack.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edu.cit.tooltrack.R
import edu.cit.tooltrack.ui.theme.LightTeal
import edu.cit.tooltrack.ui.theme.Teal
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import edu.cit.tooltrack.utils.SessionManager

@Composable
fun ProfileSettings(navController: NavHostController) {
    // Get the current context
    val context = LocalContext.current
    // Initialize SessionManager
    val sessionManager = remember { SessionManager(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp) // Increased padding to ensure Save button is fully visible
        ) {
            // Profile Header with user info and edit functionality
            ProfileSettingsHeader(
                navController = navController,
                firstName = sessionManager.getUserFirstName(),
                lastName = sessionManager.getUserLastName(),
                role = sessionManager.getUserRole()
            )

            // Profile form fields
            ProfileForm(
                firstName = sessionManager.getUserFirstName(),
                lastName = sessionManager.getUserLastName(),
                email = sessionManager.getUserEmail()
            )

            // Save button
            SaveButton()
        }
    }
}

@Composable
private fun ProfileSettingsHeader(
    navController: NavHostController,
    firstName: String = "",
    lastName: String = "",
    role: String = ""
) {
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
                text = "Edit Profile",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // User info section with profile picture
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image with edit button
                Box(
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    // Profile image
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_cat),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Edit button overlay
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .background(Teal, CircleShape)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Name - Display combined first and last name from parameters
                val displayName = if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
                    "$firstName $lastName".trim()
                } else {
                    "User"
                }

                Text(
                    text = displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // User Role - Use the role from SessionManager
                Text(
                    text = role.ifEmpty { "User" },
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ProfileForm(
    firstName: String = "",
    lastName: String = "",
    email: String = ""
) {
    // Get the current context
    val context = LocalContext.current
    // Initialize SessionManager
    val sessionManager = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp), // Reduced vertical padding
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First Name Field
        ProfileFormField(
            icon = Icons.Default.Person,
            value = firstName.ifEmpty { "First Name" },
            iconTint = Color(0xFF909090),
            label = "First Name"
        )

        // Last Name Field
        ProfileFormField(
            icon = Icons.Default.Person,
            value = lastName.ifEmpty { "Last Name" },
            iconTint = Color(0xFF909090),
            label = "Last Name"
        )

        // Email Field - Use the email from SessionManager
        ProfileFormField(
            icon = Icons.Default.Email,
            value = email.ifEmpty { "user@example.com" },
            iconTint = Color(0xFF909090)
        )

        // Password Field
        ProfileFormField(
            icon = Icons.Default.Lock,
            value = "************",
            iconTint = Color(0xFF909090)
        )

        // Phone number removed as it's not included in JWT token
    }
}

@Composable
private fun ProfileFormField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    iconTint: Color = Color.Black,
    label: String = ""
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFDEDEDE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            if (label.isNotEmpty()) {
                Column {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
            } else {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun SaveButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp), // Reduced vertical padding
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /* Handle save */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Teal
            )
        ) {
            Text(
                text = "Save",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSettingsPreview() {
    ToolTrackTheme(dynamicColor = false, darkTheme = false) {
        Surface(color = Color.White) {
            ProfileSettings(rememberNavController())
        }
    }
}