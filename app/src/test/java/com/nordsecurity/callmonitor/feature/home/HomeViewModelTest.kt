package com.nordsecurity.callmonitor.feature.home

import androidx.lifecycle.SavedStateHandle
import com.nordsecurity.callmonitor.core.model.CallResource
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.testing.repository.TestCallResourceRepository
import com.nordsecurity.callmonitor.core.testing.repository.TestUserDataRepository
import com.nordsecurity.callmonitor.core.testing.util.MainDispatcherRule
import com.nordsecurity.callmonitor.core.testing.util.TestIpAddressProvider
import com.nordsecurity.callmonitor.core.testing.util.TestServerServiceController
import com.nordsecurity.callmonitor.core.testing.util.TestSyncManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals


class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()
    private val userDataRepository = TestUserDataRepository()
    private val callResourceRepository = TestCallResourceRepository()
    private val ipAddressProvider = TestIpAddressProvider()
    private val serverServiceController = TestServerServiceController()
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel(
            savedStateHandle = savedStateHandle,
            ipAddressProvider = ipAddressProvider,
            serverServiceController = serverServiceController,
            callResourceRepository = callResourceRepository,
            syncManager = syncManager,
            userDataRepository = userDataRepository
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(HomeFeedUiState.Loading, viewModel.feedState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoCalls() = runTest {
        syncManager.setSyncing(true)

        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect {} }

        assertEquals(
            true,
            viewModel.isSyncing.value,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun callResourcesUpdatesAfterLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.feedState.collect {} }
        callResourceRepository.sendCallResources(sampleCallResources)


        val expected = HomeFeedUiState.Success(
            feed =
            sampleCallResources,
        )
        assertEquals(expected, viewModel.feedState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun ipAddressUpdatesAfterLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.ipAddress.collect {} }
        assertEquals(mockedAddress, viewModel.ipAddress.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun serverStatusUpdatesAfterLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.serverStatus.collect {} }
        userDataRepository.setServerStatus(mockedServerStatus)
        assertEquals(mockedServerStatus, viewModel.serverStatus.value)
    }

}

val mockedServerStatus = ServerStatus(isRunning = true, startTime = "2025-02-21T19:01:35.203Z")
const val mockedAddress = "192.168.0.1"
val sampleCallResources = listOf(
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