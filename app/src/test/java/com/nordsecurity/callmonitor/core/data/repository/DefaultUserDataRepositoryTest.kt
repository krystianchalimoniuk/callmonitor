package com.nordsecurity.callmonitor.core.data.repository

import com.nordsecurity.callmonitor.core.analytics.NoOpAnalyticsHelper
import com.nordsecurity.callmonitor.core.data.repositories.DefaultUserDataRepository
import com.nordsecurity.callmonitor.core.datastore.CallMonitorPreferenceDataSource
import com.nordsecurity.callmonitor.core.datastore.UserPreferences
import com.nordsecurity.callmonitor.core.datastore.test.InMemoryDataStore
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.model.UserData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DefaultUserDataRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: DefaultUserDataRepository

    private lateinit var niaPreferencesDataSource: CallMonitorPreferenceDataSource


    @Before
    fun setup() {
        niaPreferencesDataSource = CallMonitorPreferenceDataSource(
            InMemoryDataStore(
                UserPreferences.getDefaultInstance()
            )
        )

        subject = DefaultUserDataRepository(
            callMonitorPreferenceDataSource = niaPreferencesDataSource,
        )
    }

    @Test
    fun defaultUserDataRepository_default_user_data_is_correct() =
        testScope.runTest {
            assertEquals(
                UserData(
                    serverStatus = ServerStatus(isRunning = false, startTime = "")
                ),
                subject.userData.first(),
            )
        }

    @Test
    fun defaultUserDataRepository_default_set_server_delegates_to_call_monitor_preferences() =
        testScope.runTest {
            subject.setServerStatus(
                ServerStatus(
                    isRunning = true,
                    startTime = "2025-02-21T19:01:35.203Z"
                )
            )
            assertEquals(
                UserData(
                    serverStatus = ServerStatus(
                        isRunning = true,
                        startTime = "2025-02-21T19:01:35.203Z"
                    )
                ),
                subject.userData.first(),
            )
        }

}