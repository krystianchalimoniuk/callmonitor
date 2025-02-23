package com.nordsecurity.callmonitor.core.data.repository

import com.nordsecurity.callmonitor.core.data.repositories.DefaultCallResourceRepository
import com.nordsecurity.callmonitor.core.data.testdoubles.TestCallLogLocalDataSource
import com.nordsecurity.callmonitor.core.data.testdoubles.TestCallResourceDao
import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.datastore.CallMonitorPreferenceDataSource
import com.nordsecurity.callmonitor.core.datastore.UserPreferences
import com.nordsecurity.callmonitor.core.datastore.test.InMemoryDataStore
import com.nordsecurity.callmonitor.core.model.ActiveCallInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DefaultCallResourceRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var subject: DefaultCallResourceRepository
    private lateinit var callResourceDao: TestCallResourceDao
    private lateinit var callMonitorPreferenceDataSource: CallMonitorPreferenceDataSource
    private lateinit var callLogLocalDataSource: TestCallLogLocalDataSource


    @Before
    fun setup() {
        callMonitorPreferenceDataSource = CallMonitorPreferenceDataSource(
            InMemoryDataStore(
                UserPreferences.getDefaultInstance()
            )
        )
        callResourceDao = TestCallResourceDao()
        callLogLocalDataSource = TestCallLogLocalDataSource()
        subject = DefaultCallResourceRepository(
            ioDispatcher = testDispatcher,
            callResourceDao = callResourceDao,
            localDataSource = callLogLocalDataSource
        )

    }

    @Test
    fun defaultCallResourceRepository_refresh_call_resources_delegates_to_local_data_source() =
        testScope.runTest {

            val list = listOf(
                CallResourceEntity(
                    id = "1",
                    timesQueried = 0,
                    callerName = "John Doe",
                    duration = 20,
                    phoneNumber = "+48 233424123",
                    startTime = 1739965430L
                ),
                CallResourceEntity(
                    id = "2",
                    timesQueried = 1,
                    callerName = "Bob Doe",
                    duration = 30,
                    phoneNumber = "+48 235424123",
                    startTime = 1739965430L,
                )
            )
            callLogLocalDataSource.sendCallResourceEntity(list)
            subject.refreshCallResources()
            assertEquals(
                expected = callResourceDao.getCallResources(false).first(),
                actual = callLogLocalDataSource.getRecentCalls(null)
            )
        }

    @Test
    fun defaultCallResourceRepository_observe_active_call_resources_return_correct_value() =
        testScope.runTest {
            val activeCallInfo =
                ActiveCallInfo(ongoing = true, phoneNumber = "+48 235424123", "Bob Doe")
            callLogLocalDataSource.sendActiveCallStatus(activeCallInfo)

            assertEquals(
                expected = subject.observeActiveCall().first(),
                actual = callLogLocalDataSource.observeActiveCall().first()
            )
        }

    @Test
    fun defaultCallResourceRepository__increment_query_resources_delegates_to_local_data_source() =
        testScope.runTest {
            val list = listOf(
                CallResourceEntity(
                    id = "1",
                    timesQueried = 0,
                    callerName = "John Doe",
                    duration = 20,
                    phoneNumber = "+48 233424123",
                    startTime = 1739965430L
                )
            )
            callLogLocalDataSource.sendCallResourceEntity(list)
            subject.refreshCallResources()
            subject.incrementQueryCounterFor(list)
            assertEquals(
                expected = callResourceDao.getCallResources(false).first().first().timesQueried,
                actual = 1
            )
        }

    @Test
    fun defaultCallResourceRepository_marking_as_viewed_delegates_to_local_data_source() =
        testScope.runTest {
            val list = listOf(
                CallResourceEntity(
                    id = "1",
                    timesQueried = 0,
                    callerName = "John Doe",
                    duration = 20,
                    phoneNumber = "+48 233424123",
                    startTime = 1739965430L,
                    isNew = true
                )
            )
            callLogLocalDataSource.sendCallResourceEntity(list)
            subject.refreshCallResources()
            subject.markAsViewed(list)
            assertEquals(
                expected = callResourceDao.getCallResources(false).first().first().isNew,
                actual = false
            )
        }
}