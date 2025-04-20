package edu.cit.tooltrack

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import edu.cit.tooltrack.BottomNavItem

object Constants {
    val BottomNavItem = listOf(
        //Home
        BottomNavItem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = "home"
        ),
        BottomNavItem(
            label = "Scan QR",
            icon = Icons.Filled.QrCode,
            route = "scan"
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = "profile"
        )
    )
}