package com.nordsecurity.callmonitor.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nordsecurity.callmonitor.app.ui.CallMonitorAppState
import androidx.navigation.compose.NavHost
import com.nordsecurity.callmonitor.feature.home.navigation.HomeRoute
import com.nordsecurity.callmonitor.feature.home.navigation.homeScreen

@Composable
fun CallMonitorNavHost(
    appState: CallMonitorAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        homeScreen(onClick = {}, onShowSnackbar = onShowSnackbar)
    }

}