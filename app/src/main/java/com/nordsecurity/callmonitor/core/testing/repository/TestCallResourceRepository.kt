package com.nordsecurity.callmonitor.core.testing.repository

import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.domain.CallResourceRepository
import com.nordsecurity.callmonitor.core.model.ActiveCallInfo
import com.nordsecurity.callmonitor.core.model.CallResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TestCallResourceRepository : CallResourceRepository {
    /**
     * The backing hot flow for the list of calls resources for testing.
     */
    private val callResourcesFlow: MutableSharedFlow<List<CallResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * The backing hot flow for the list of topics ids for testing.
     */
    private val activeCallInfoFlow: MutableSharedFlow<ActiveCallInfo> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override suspend fun refreshCallResources() = Unit

    override fun getCallResources(useNew: Boolean): Flow<List<CallResource>> =
        callResourcesFlow.map { callResource ->
            callResource
        }


    override suspend fun incrementQueryCounterFor(callResourceEntities: List<CallResourceEntity>) {
        callResourcesFlow.map { it.map { call -> call.copy(timesQueried = call.timesQueried + 1) } }
    }

    override suspend fun markAsViewed(callResourceEntities: List<CallResourceEntity>) {
    }

    override fun observeActiveCall(): Flow<ActiveCallInfo?> = activeCallInfoFlow
    override fun observeCallLogChanges(): Flow<Unit> {
        return flowOf(Unit)
    }

    /**
     * A test-only API to allow controlling the list of call resources from tests.
     */
    fun sendCallResources(callResources: List<CallResource>) {
        callResourcesFlow.tryEmit(callResources)
    }

    fun refreshActiveCallInfo(activeCallInfo: ActiveCallInfo) {
        activeCallInfoFlow.tryEmit(activeCallInfo)
    }
}