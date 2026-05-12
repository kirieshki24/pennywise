package com.example.pennywise

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import org.junit.Rule
import org.junit.Test

class HomeFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addProfileAndTransaction() {
        composeTestRule.onNodeWithText("Profiles").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("profile_name_input").performTextInput("Main")
        composeTestRule.onNodeWithTag("profile_limit_input").performTextInput("35")
        composeTestRule.onNodeWithTag("profile_add_button").performClick()

        composeTestRule.onNodeWithText("Main").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("add_entry_fab").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("entry_amount_input").performTextInput("10")
        composeTestRule.onNodeWithTag("entry_note_input").performTextInput("Coffee")
        composeTestRule.onNodeWithTag("entry_profile_dropdown").performClick()
        composeTestRule
            .onAllNodesWithText("Main")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.onNodeWithTag("entry_save_button").performClick()

        composeTestRule.onNodeWithText("Coffee").assertIsDisplayed()
    }
}
