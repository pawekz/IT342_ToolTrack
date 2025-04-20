package edu.cit.tooltrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigationWorks_whenBottomNavItemsClicked() {
        // Verify we start on Home screen
        composeTestRule.onNodeWithText("Welcome to the Home Screen").assertIsDisplayed()

        // Navigate to Scan screen
        composeTestRule.onNodeWithText("Scan QR").performClick()
        composeTestRule.onNodeWithText("Welcome to the Scan Screen").assertIsDisplayed()

        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Welcome to the Profile Screen").assertIsDisplayed()

        // Navigate back to Home
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Welcome to the Home Screen").assertIsDisplayed()
    }
}