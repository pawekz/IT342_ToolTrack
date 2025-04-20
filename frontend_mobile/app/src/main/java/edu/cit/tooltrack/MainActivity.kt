package edu.cit.tooltrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import androidx.compose.ui.tooling.preview.Preview

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
                            BottomNavItem(navController = navController)
                        },
                        content = { padding ->
                            NavHostContainer(navController = navController, padding = padding)
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
        modifier = Modifier.padding(padding),
        builder = {
            composable("home") {
                HomeScreen()
            }
            composable("scan") {
                ScanScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
        }
    )
}

@Composable
fun BottomNavItem(navController: NavHostController) {
    NavigationBar(
        containerColor = Color(0xFFFFFFFF)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Constants.BottomNavItem.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route)
                },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.label
                    )
                },
                label = {
                    Text(text = navItem.label)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2EA69E),
                    unselectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color(0xFF2EA69E)
                )
            )
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
                bottomBar = {
                    BottomNavItem(navController = navController)
                },
                content = { padding ->
                    NavHostContainer(navController = navController, padding = padding)
                }
            )
        }
    }
}