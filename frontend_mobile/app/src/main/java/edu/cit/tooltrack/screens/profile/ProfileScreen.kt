package edu.cit.tooltrack.screens.profile

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edu.cit.tooltrack.LoginActivity
import edu.cit.tooltrack.R
import edu.cit.tooltrack.ui.theme.LightTeal
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import edu.cit.tooltrack.utils.SessionManager
import edu.cit.tooltrack.screens.add.AddToolActivity

// Import the proper BorrowedToolActivity class or use a placeholder if it doesn't exist yet
// If the class doesn't exist yet, comment out the relevant code that uses it
// import edu.cit.tooltrack.screens.borrowed.BorrowedToolActivity

@Composable
fun ProfileScreen(navController: NavHostController) {
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
                .padding(bottom = 80.dp) // Add padding for bottom navigation
        ) {
            // Profile Header with user info
            ProfileHeader(
                firstName = sessionManager.getUserFirstName(),
                lastName = sessionManager.getUserLastName(),
                email = sessionManager.getUserEmail(),
                navController = navController
            )

            // Menu Options - pass sessionManager
            MenuOptions(
                navController = navController,
                sessionManager = sessionManager,
                onLogoutClick = {
                    // Check if the user is actually logged in and token is valid
                    if (sessionManager.isLoggedIn()) {
                        // Clear the session data (removes JWT token and user info)
                        sessionManager.clearSession()
                        Log.d("ProfileScreen", "User logged out successfully")
                    } else {
                        Log.d("ProfileScreen", "User was already logged out due to token expiration")
                    }

                    // Navigate to login screen regardless of previous state
                    context.startActivity(Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    firstName: String = "",
    lastName: String = "",
    email: String = "",
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LightTeal, // Teal background fills from the top
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
    ) {
        // Title
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
                text = "Profile",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // User info section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
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

                Spacer(modifier = Modifier.height(16.dp))

                // User Name - Display combined first and last name
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

                // User Email - Use the email from SessionManager
                Text(
                    text = email.ifEmpty { "@user@example.com" },
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MenuOptions(
    navController: NavHostController,
    sessionManager: SessionManager, // SessionManager parameter is now properly passed
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Borrowed Tools Option
        MenuOption(
            icon = Icons.Default.AccountBalanceWallet,
            title = "Borrowed Tools",
            showDivider = true,
            onClick = { 
                // Handle the click safely without directly launching an Activity that might not exist
                try {
                    // Try to find the BorrowedToolActivity class dynamically
                    val borrowedActivityClass = Class.forName("edu.cit.tooltrack.screens.borrowed.BorrowedToolActivity")
                    context.startActivity(Intent(context, borrowedActivityClass))
                } catch (e: ClassNotFoundException) {
                    Log.e("ProfileScreen", "BorrowedToolActivity not found", e)
                    // Show a toast or other feedback that this feature is coming soon
                }
            }
        )

        // Add Tool (only visible for Admin users)
        val userRole = sessionManager.getUserRole()
        if (userRole.equals("Admin", ignoreCase = true)) {
            MenuOption(
                icon = Icons.Default.Add,
                title = "Add Tool",
                iconColor = Color.Green,
                showDivider = true,
                onClick = { 
                    // Navigate to AddToolActivity
                    context.startActivity(Intent(context, AddToolActivity::class.java))
                }
            )
        }

        // Profile Settings Option
        MenuOption(
            icon = Icons.Default.Person,
            title = "Profile Settings",
            showDivider = true,
            onClick = { navController.navigate("profile_settings") }
        )

        // About Us Option
        MenuOption(
            icon = Icons.Outlined.Info,
            title = "About us",
            showDivider = true,
            onClick = { navController.navigate("about") }
        )

        // Delete Account Option
        MenuOption(
            icon = Icons.Outlined.Delete,
            title = "Delete Account",
            textColor = Color.Red,
            iconColor = Color.Red,
            showDivider = false,
            onClick = { /* Show delete account confirmation */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Option
        MenuOption(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Logout",
            showDivider = false,
            onClick = onLogoutClick
        )
    }
}

@Composable
private fun MenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    textColor: Color = Color.Black,
    iconColor: Color = Color.Black, // Fixed the parameter declaration
    showDivider: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in a circular container with shadow
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape
                    )
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(19.dp))

            // Menu item text
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }

        if (showDivider) {
            Divider(
                color = Color(0xFFD4D4D4),
                thickness = 1.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ToolTrackTheme(dynamicColor = false, darkTheme = false) {
        Surface(color = Color.White) {
            ProfileScreen(rememberNavController())
        }
    }
}
