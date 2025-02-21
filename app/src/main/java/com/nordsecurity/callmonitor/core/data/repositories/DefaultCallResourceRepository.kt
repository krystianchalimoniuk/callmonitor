package com.nordsecurity.callmonitor.core.data.repositories

import com.nordsecurity.callmonitor.core.common.network.CallMonitorDispatchers
import com.nordsecurity.callmonitor.core.common.network.Dispatcher
import com.nordsecurity.callmonitor.core.database.dao.CallResourceDao
import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.database.model.asExternalModel
import com.nordsecurity.callmonitor.core.domain.CallLogLocalDataSource
import com.nordsecurity.callmonitor.core.domain.CallResourceRepository
import com.nordsecurity.callmonitor.core.model.ActiveCallInfo
import com.nordsecurity.callmonitor.core.model.CallResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultCallResourceRepository @Inject constructor(
    private val localDataSource: CallLogLocalDataSource,
    private val callResourceDao: CallResourceDao,
    @Dispatcher(CallMonitorDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) :
    CallResourceRepository {
    override suspend fun refreshCallResources() = withContext(ioDispatcher) {
        val lastStartTime = callResourceDao.getLastStartTime()
        val recentCalls = localDataSource.getRecentCalls(lastStartTime)
        callResourceDao.upsertCallResources(recentCalls)
    }

    override fun getCallResources(useNew: Boolean): Flow<List<CallResource>> {
        return callResourceDao.getCallResources(useNew = useNew).map {it.map(CallResourceEntity::asExternalModel)  }
    }

    override suspend fun incrementQueryCounterFor(callResourceEntities: List<CallResourceEntity>) {
        callResourceDao.upsertCallResources(callResourceEntities.map { it.copy(timesQueried = it.timesQueried + 1) })
    }

    override suspend fun markAsViewed(callResourceEntities: List<CallResourceEntity>) {
        callResourceDao.upsertCallResources(callResourceEntities.map { it.copy(isNew = false) })
    }

    override fun observeActiveCall(): Flow<ActiveCallInfo?> {
        return localDataSource.observeActiveCall()
    }
}