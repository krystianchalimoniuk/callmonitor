package com.nordsecurity.callmonitor.feature.home

import android.Manifest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.nordsecurity.callmonitor.R
import com.nordsecurity.callmonitor.core.designsystem.component.CallMonitorButton
import com.nordsecurity.callmonitor.core.designsystem.component.CallMonitorOverlayLoadingWheel
import com.nordsecurity.callmonitor.core.designsystem.icon.CallMonitorIcons
import com.nordsecurity.callmonitor.core.model.CallResource
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.ui.TrackScreenViewEvent
import com.nordsecurity.callmonitor.core.ui.callsFeed


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val ipAddress by viewModel.ipAddress.collectAsStateWithLifecycle()
    val feedUiState by viewModel.feedState.collectAsStateWithLifecycle()
    val serverStatus by viewModel.serverStatus.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()


    HomeScreen(
        modifier = modifier,
        ipAddress = ipAddress,
        feedUiState = feedUiState,
        isSyncing = isSyncing,
        serverStatus = serverStatus,
        onReadCallPermissionGranted = { viewModel.refreshCallsList() },
        onStartServer = { viewModel.startServer() },
        onStopServer = { viewModel.stopServer() },
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    ipAddress: String?,
    feedUiState: HomeFeedUiState,
    isSyncing: Boolean,
    serverStatus: ServerStatus,
    onReadCallPermissionGranted: () -> Unit,
    onStartServer: () -> Unit,
    onStopServer: () -> Unit,
) {

    val isFeedLoading = feedUiState is HomeFeedUiState.Loading
    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isFeedLoading }
    val state = rememberLazyStaggeredGridState()

    Column(modifier = modifier.padding(16.dp).fillMaxSize()) {
        IpAddressText(ipAddress = "IP address: http://$ipAddress:8080" ?: "unknown")
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag("calls:feed").weight(1f),
            state = state,
        ) {
            callsFeed(feedUiState = feedUiState)
        }
        AnimatedVisibility(
            visible = isSyncing || isFeedLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_home_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                CallMonitorOverlayLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        if (serverStatus.isRunning) {
            val loadingContentDescription = stringResource(id = R.string.server_is_running)

            CallMonitorOverlayLoadingWheel(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                contentDesc = loadingContentDescription,
            )
            Spacer(modifier = Modifier.height(24.dp))

            CallMonitorButton(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                onClick = onStopServer,
                text = { Text(stringResource(R.string.stop_server)) },
                leadingIcon = {
                    Icon(
                        imageVector = CallMonitorIcons.Stop,
                        contentDescription = null
                    )
                },
            )
        } else {
            CallMonitorButton(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                onClick = onStartServer,
                text = { Text(stringResource(R.string.start_server)) },
                leadingIcon = {
                    Icon(
                        imageVector = CallMonitorIcons.Start,
                        contentDescription = null
                    )
                },
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
    TrackScreenViewEvent(screenName = "Home")
    RequestAppPermissions(onReadCallPermissionGranted)
}

@Composable
fun IpAddressText(
    ipAddress: String,
    modifier: Modifier = Modifier,
) {
    SelectionContainer {
        Text(
            ipAddress,
            style = MaterialTheme.typography.headlineSmall,
            modifier = modifier,
        )
    }
}


@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun RequestAppPermissions(
    onAllPermissionsGranted: () -> Unit = {}
) {
    if (LocalInspectionMode.current) return

    val permissionsToRequest = remember {
        mutableListOf<String>().apply {
            add(Manifest.permission.READ_PHONE_STATE)
            add(Manifest.permission.READ_CALL_LOG)
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (VERSION.SDK_INT < VERSION_CODES.S) {
                add(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = permissionsToRequest) {
        if (it[Manifest.permission.READ_CALL_LOG] == true) {
            onAllPermissionsGranted()
        }
    }

    LaunchedEffect(permissionsState) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        ipAddress = "192.168.1.1",
        feedUiState = HomeFeedUiState.Success(
            arrayListOf(
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
        ),
        serverStatus = ServerStatus(
            startTime = System.currentTimeMillis().toString(),
            isRunning = false
        ),
        isSyncing = false,
        modifier = Modifier,
        onReadCallPermissionGranted = {},
        onStartServer = {},
        onStopServer = {}
    )
}