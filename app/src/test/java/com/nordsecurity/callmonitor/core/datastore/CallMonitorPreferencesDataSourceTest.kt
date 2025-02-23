package com.nordsecurity.callmonitor.core.datastore

import com.nordsecurity.callmonitor.core.datastore.test.InMemoryDataStore
import com.nordsecurity.callmonitor.core.model.ServerStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


class CallMonitorPreferencesDataSourceTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: CallMonitorPreferenceDataSource

    @Before
    fun setup() {
        subject =
            CallMonitorPreferenceDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
    }


    @Test
    fun shouldServerStatusIsRunningFalseAndStartTimeEmptyByDefault() = testScope.runTest {
        val defaultServerStatus = ServerStatus(startTime = "", isRunning = false)
        assertEquals(defaultServerStatus, subject.userData.first().serverStatus)
    }

    @Test
    fun userShouldServerStatusIsRunningIsTrueWhenSet() = testScope.runTest {
        val serverStatusToBeSet =
            ServerStatus(isRunning = true, startTime = "2025-02-21T18:58:09.771Z")
        subject.setServerStatus(serverStatusToBeSet)
        assertEquals(serverStatusToBeSet, subject.userData.first().serverStatus)
    }


}