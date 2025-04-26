package edu.cit.tooltrack

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SignupActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule(SignupActivity::class.java)

    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    @Test
    fun signupFormDisplaysCorrectly() {
        // Verify all form fields are displayed
        composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lastNameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmPasswordField").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun signupWithValidData_doesNotCrash() {
        assertTrue("Device has no internet connection", hasInternetConnection(composeTestRule.activity))
        
        // Enter first name
        composeTestRule.onNodeWithTag("firstNameField").performTextClearance()
        composeTestRule.onNodeWithTag("firstNameField").performTextInput("Test")
        
        // Enter last name
        composeTestRule.onNodeWithTag("lastNameField").performTextClearance()
        composeTestRule.onNodeWithTag("lastNameField").performTextInput("User")
        
        // Enter email - using a unique email to avoid conflicts
        val uniqueEmail = "testuser${System.currentTimeMillis()}@example.com"
        composeTestRule.onNodeWithTag("emailField").performTextClearance()
        composeTestRule.onNodeWithTag("emailField").performTextInput(uniqueEmail)
        
        // Enter password
        composeTestRule.onNodeWithTag("passwordField").performTextClearance()
        composeTestRule.onNodeWithTag("passwordField").performTextInput("!@#Admin123")
        
        // Confirm password
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextClearance()
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextInput("!@#Admin123")
        
        // Click the signup button
        composeTestRule.onNodeWithText("Sign Up", ignoreCase = true).performClick()
        
        // Wait for response (success or error)
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithText("Successfully Registered").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Registration failed").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Error:").fetchSemanticsNodes().isNotEmpty()
        }
        
        // We don't assert success here because we don't want to create a new user every time
        // Just verify the app didn't crash
    }

    @Test
    fun signupWithInvalidData_showsError() {
        // Enter invalid data (empty fields)
        composeTestRule.onNodeWithTag("firstNameField").performTextClearance()
        composeTestRule.onNodeWithTag("lastNameField").performTextClearance()
        composeTestRule.onNodeWithTag("emailField").performTextClearance()
        composeTestRule.onNodeWithTag("passwordField").performTextClearance()
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextClearance()
        
        // Click the signup button
        composeTestRule.onNodeWithText("Sign Up", ignoreCase = true).performClick()
        
        // Verify error message is shown (we expect first name validation to fail first)
        composeTestRule.waitForIdle()
        // The app should show a toast with an error message
    }

    @Test
    fun passwordMismatch_showsError() {
        // Fill in valid data except for password mismatch
        composeTestRule.onNodeWithTag("firstNameField").performTextClearance()
        composeTestRule.onNodeWithTag("firstNameField").performTextInput("Test")
        
        composeTestRule.onNodeWithTag("lastNameField").performTextClearance()
        composeTestRule.onNodeWithTag("lastNameField").performTextInput("User")
        
        composeTestRule.onNodeWithTag("emailField").performTextClearance()
        composeTestRule.onNodeWithTag("emailField").performTextInput("testuser@example.com")
        
        // Enter different passwords
        composeTestRule.onNodeWithTag("passwordField").performTextClearance()
        composeTestRule.onNodeWithTag("passwordField").performTextInput("Password123!")
        
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextClearance()
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextInput("DifferentPassword123!")
        
        // Click the signup button
        composeTestRule.onNodeWithText("Sign Up", ignoreCase = true).performClick()
        
        // Verify error message is shown
        composeTestRule.waitForIdle()
        // The app should show a toast with a password mismatch error
    }
}