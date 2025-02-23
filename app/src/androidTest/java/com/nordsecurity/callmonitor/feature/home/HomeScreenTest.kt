package com.nordsecurity.callmonitor.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.nordsecurity.callmonitor.R
import com.nordsecurity.callmonitor.core.model.CallResource
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

    @Test
    fun feed_whenLoaded_showsFeed() {
        composeTestRule.setContent {
            Box {
                HomeScreen(
                    isSyncing = false,
                    feedUiState = HomeFeedUiState.Success(sampleCallResources),
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
            .onNodeWithText(
                sampleCallResources[0].phoneNumber,
                substring = true,
            )
            .assertExists()

        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(
                hasText(
                    sampleCallResources[1].phoneNumber,
                    substring = true,
                ),
            )

        composeTestRule
            .onNodeWithText(
                sampleCallResources[1].phoneNumber,
                substring = true,
            )
            .assertExists()
    }

    @Test
    fun serverStatus_whenNotRunning_showsStartButton() {
        composeTestRule.setContent {
            Box {
                HomeScreen(
                    isSyncing = false,
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
            .onNodeWithText(
                composeTestRule.activity.resources.getString(R.string.start_server),
                substring = true,
            )
            .assertExists()
    }

    @Test
    fun serverStatus_whenRunning_showsStopButton() {
        composeTestRule.setContent {
            Box {
                HomeScreen(
                    isSyncing = false,
                    feedUiState = HomeFeedUiState.Success(emptyList()),
                    serverStatus = ServerStatus(
                        isRunning = true,
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
            .onNodeWithText(
                composeTestRule.activity.resources.getString(R.string.stop_server),
                substring = true,
            )
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.server_is_running),
            )
            .assertExists()
    }

    @Test
    fun ipAddress_whenLoaded_showsIpAddress() {
        composeTestRule.setContent {
            Box {
                HomeScreen(
                    isSyncing = false,
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
            .onNodeWithText(
                mockedAddress,
                substring = true,
            )
            .assertExists()
    }
}

const val mockedAddress = "IP address: http://192.168.0.1:8080"

private val sampleCallResources = listOf(
    CallResource(
        id = "1",
        callerName = "John Doe",
        phoneNumber = "+48 233424123",
        duration = 20,
        startTime = 1739965430L,
        timesQueried = 0
    ), CallResource(
        id = "2",
        callerName = "Alice Doe",
        phoneNumber = "+43 533424123",
        duration = 12000,
        startTime = 1739965430L,
        timesQueried = 1
    ), CallResource(
        id = "3",
        callerName = "Bob Doe",
        phoneNumber = "+42 733424123",
        duration = 320,
        startTime = 1739965430L,
        timesQueried = 2
    )
)