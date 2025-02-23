package com.nordsecurity.callmonitor.core.domain

import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.model.ActiveCallInfo
import kotlinx.coroutines.flow.Flow

interface CallLogLocalDataSource {
    suspend fun getRecentCalls(lastStartTime: Long? = null): List<CallResourceEntity>
    fun observeActiveCall(): Flow<ActiveCallInfo?>
    fun observeCallLogChanges(): Flow<Unit>
}