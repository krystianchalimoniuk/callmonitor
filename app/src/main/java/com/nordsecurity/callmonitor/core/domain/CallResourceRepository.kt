package com.nordsecurity.callmonitor.core.domain

import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.model.ActiveCallInfo
import com.nordsecurity.callmonitor.core.model.CallResource
import kotlinx.coroutines.flow.Flow

interface CallResourceRepository {
    suspend fun refreshCallResources()
    fun getCallResources(useNew: Boolean = false): Flow<List<CallResource>>
    suspend fun incrementQueryCounterFor(callResourceEntities: List<CallResourceEntity>)
    suspend fun markAsViewed(callResourceEntities: List<CallResourceEntity>)
    fun observeActiveCall(): Flow<ActiveCallInfo?>
    fun observeCallLogChanges(): Flow<Unit>

}