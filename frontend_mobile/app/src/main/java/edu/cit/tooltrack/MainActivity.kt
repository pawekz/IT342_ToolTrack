package edu.cit.tooltrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.cit.tooltrack.screens.about.AboutScreen
import edu.cit.tooltrack.screens.home.HomeScreen
import edu.cit.tooltrack.screens.profile.ProfileScreen
import edu.cit.tooltrack.screens.profile.ProfileSettings
import edu.cit.tooltrack.screens.scan.BorrowRequestToolScreen
import edu.cit.tooltrack.screens.scan.ScanScreen
import edu.cit.tooltrack.ui.theme.ToolTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolTrackTheme(dynamicColor = false, darkTheme = false) {
                val navController = rememberNavController()
                Surface(color = Color.White) {
                    Scaffold(
                        bottomBar = {
                            // Remove the unnecessary Column and Spacer that's pushing the navigation bar up
                            BottomNavItem(navController)
                        },
                        content = { padding ->
                            NavHostContainer(navController, padding)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(padding)
    ) {
        composable("home") { HomeScreen() }
        composable("scan") { ScanScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("profile_settings") { ProfileSettings(navController) }
        composable("about") { AboutScreen(navController) }

        // Add this route for the borrow request screen
        composable(
            route = "borrowRequest/{toolId}",
            arguments = listOf(navArgument("toolId") { type = NavType.StringType })
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
            BorrowRequestToolScreen(navController = navController, toolId = toolId)
        }
    }
}

@Composable
fun BottomNavItem(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp,
        color = Color(0xFFFFFFFF)
    ) {
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Constants.BottomNavItem.forEach { navItem ->
                NavigationBarItem(
                    selected = currentRoute == navItem.route,
                    onClick = { navController.navigate(navItem.route) },
                    icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                    label = { Text(text = navItem.label) },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        indicatorColor = Color(0xFF2EA69E), // green indicator
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivityContent() {
    ToolTrackTheme(dynamicColor = false, darkTheme = false) {
        val navController = rememberNavController()
        Surface(color = Color.White) {
            Scaffold(
                bottomBar = { BottomNavItem(navController) },
                content = { padding ->
                    // Directly display HomeScreen instead of a placeholder.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        HomeScreen()
                    }
                }
            )
        }
    }
}
