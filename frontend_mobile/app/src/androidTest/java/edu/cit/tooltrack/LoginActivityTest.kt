package edu.cit.tooltrack

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule(LoginActivity::class.java)

    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    @Test
    fun loginWithValidCredentials_doesNotCrashAndNavigates() {
        assertTrue("Device has no internet connection", hasInternetConnection(composeTestRule.activity))
        // Enter email
        composeTestRule.onNodeWithTag("emailField").performTextClearance()
        composeTestRule.onNodeWithTag("emailField").performTextInput("paula.binignit@cit.edu")
        // Enter password
        composeTestRule.onNodeWithTag("passwordField").performTextClearance()
        composeTestRule.onNodeWithTag("passwordField").performTextInput("!@#Admin123")
        // Click the login button (using text label)
        composeTestRule.onNodeWithText("Sign In", ignoreCase = true).performClick()
        // Wait for either success or error snackbar
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithText("Login successful, redirecting to Dashboard").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Username or Password is incorrect").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Error:").fetchSemanticsNodes().isNotEmpty()
        }
        // Assert success or detailed error for easier debugging
        val success = composeTestRule.onAllNodesWithText("Login successful, redirecting to Dashboard").fetchSemanticsNodes().isNotEmpty()
        val error = composeTestRule.onAllNodesWithText("Username or Password is incorrect").fetchSemanticsNodes().isNotEmpty()
        val genericError = composeTestRule.onAllNodesWithText("Error:").fetchSemanticsNodes().isNotEmpty()
        assertTrue("Expected login success, got error or nothing (check credentials, backend, or network)", success)
    }

    @Test
    fun loginWithInvalidCredentials_showsErrorSnackbar() {
        assertTrue("Device has no internet connection", hasInternetConnection(composeTestRule.activity))
        // Enter fake email
        composeTestRule.onNodeWithTag("emailField").performTextClearance()
        composeTestRule.onNodeWithTag("emailField").performTextInput("paula.binignit1")
        // Enter fake password
        composeTestRule.onNodeWithTag("passwordField").performTextClearance()
        composeTestRule.onNodeWithTag("passwordField").performTextInput("12345")
        // Click the login button
        composeTestRule.onNodeWithText("Sign In", ignoreCase = true).performClick()
        // Wait for error snackbar
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithText("Username or Password is incorrect").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Error:").fetchSemanticsNodes().isNotEmpty()
        }
        val error = composeTestRule.onAllNodesWithText("Username or Password is incorrect").fetchSemanticsNodes().isNotEmpty()
        val genericError = composeTestRule.onAllNodesWithText("Error:").fetchSemanticsNodes().isNotEmpty()
        assertTrue("Expected error snackbar for invalid credentials", error || genericError)
    }
}
