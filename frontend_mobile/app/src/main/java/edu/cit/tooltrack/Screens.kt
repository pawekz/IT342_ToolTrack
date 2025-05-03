package edu.cit.tooltrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen() {
    // Your Home screen content goes here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Add your UI components here
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home Icon",
            tint = Color.Black
        )
        Text(
            text = "Welcome to the Home Screen",
            color = Color.Black
        )
    }
}

@Composable
fun ScanScreen() {
    // Your Scan screen content goes here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        // Add your UI components here
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Scan Icon",
            tint = Color.Black
        )
        Text(
            text = "Welcome to the Scan Screen",
            color = Color.Black
        )
    }
}

@Composable
fun ProfileScreen() {
    // Your Profile screen content goes here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        // Add your UI components here
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Profile Icon",
            tint = Color.Black
        )
        Text(
            text = "Welcome to the Profile Screen",
            color = Color.Black
        )
    }
}