package com.nordsecurity.callmonitor.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import com.nordsecurity.callmonitor.feature.home.HomeRoute


@Serializable
object HomeRoute

fun NavController.navigateHome(navOptions: NavOptions) =
    navigate(HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen(
    onClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<HomeRoute> {
        HomeRoute()
    }
}

