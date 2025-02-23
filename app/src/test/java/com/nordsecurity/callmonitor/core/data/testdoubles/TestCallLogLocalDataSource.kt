package com.nordsecurity.callmonitor.core.data.testdoubles

import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.domain.CallLogLocalDataSource
import com.nordsecurity.callmonitor.core.model.ActiveCallInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class TestCallLogLocalDataSource : CallLogLocalDataSource {

    private val entitiesStateFlow = MutableStateFlow(emptyList<CallResourceEntity>())
    private val activeCallStateFlow: MutableStateFlow<ActiveCallInfo?> = MutableStateFlow(null)
    private val callLogChangesStateFlow: MutableStateFlow<Unit> = MutableStateFlow(Unit)
    override suspend fun getRecentCalls(lastStartTime: Long?): List<CallResourceEntity> {
        return entitiesStateFlow.first()
    }

    override fun observeActiveCall(): Flow<ActiveCallInfo?> {
        return activeCallStateFlow
    }

    override fun observeCallLogChanges(): Flow<Unit> {
        return callLogChangesStateFlow
    }

    fun sendCallResourceEntity(callResourceEntities: List<CallResourceEntity>) {
        entitiesStateFlow.update { oldValues ->
            // New values come first so they overwrite old values
            (callResourceEntities + oldValues)
                .distinctBy(CallResourceEntity::id)
                .sortedWith(
                    compareBy(CallResourceEntity::startTime).reversed(),
                )
        }
    }

    fun sendActiveCallStatus(activeCallInfo: ActiveCallInfo) {
        activeCallStateFlow.value = activeCallInfo
    }

    fun sendCallLogChangedFlow() {
        callLogChangesStateFlow.value = Unit
    }
}