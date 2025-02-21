package com.nordsecurity.callmonitor.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.nordsecurity.callmonitor.R
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.testing.rules.GrantPostNotificationsPermissionRule
import com.nordsecurity.callmonitor.core.testing.rules.GrantReadCallLogPermissionRule
import com.nordsecurity.callmonitor.core.testing.rules.GrantReadContactsPermissionRule
import com.nordsecurity.callmonitor.core.testing.rules.GrantReadPhoneStatePermissionRule
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule(order = 0)
    val postNotificationsPermission = GrantPostNotificationsPermissionRule()

    @get:Rule(order = 1)
    val readCallLogPermission = GrantReadCallLogPermissionRule()

    @get:Rule(order = 2)
    val readPhoneStatePermission = GrantReadPhoneStatePermissionRule()

    @get:Rule(order = 3)
    val readContactsPermission = GrantReadContactsPermissionRule()

    @get:Rule(order = 4)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            Box {
                HomeScreen(
                    isSyncing = false,
                    feedUiState = HomeFeedUiState.Loading,
                    serverStatus = ServerStatus(
                        isRunning = false,
                        startTime = "2025-02-21T19:01:35.203Z"
                    ),
                    ipAddress = "192.168.0.1",
                    modifier = Modifier,
                    onStartServer = {},
                    onStopServer = {},
                    onReadCallPermissionGranted = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_home_loading),
            )
            .assertExists()
    }

    @Test
    fun circularProgressIndicator_whenScreenIsSyncing_exists() {
        composeTestRule.setContent {
            Box {
                HomeScreen(
                    isSyncing = true,
                    feedUiState = HomeFeedUiState.Success(emptyList()),
                    serverStatus = ServerStatus(
                        isRunning = false,
                        startTime = "2025-02-21T19:01:35.203Z"
                    ),
                    ipAddress = "192.168.0.1",
                    modifier = Modifier,
                    onStartServer = {},
                    onStopServer = {},
                    onReadCallPermissionGranted = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.feature_home_loading),
            )
            .assertExists()
    }


}