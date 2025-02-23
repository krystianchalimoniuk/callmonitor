package com.nordsecurity.callmonitor.core.data.testdoubles

import com.nordsecurity.callmonitor.core.database.dao.CallResourceDao
import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestCallResourceDao : CallResourceDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<CallResourceEntity>())

    override fun getCallResources(useNew: Boolean): Flow<List<CallResourceEntity>> =
        entitiesStateFlow.map { callResourceEntities ->
            if (useNew) {
                callResourceEntities.filter { it.isNew }
            } else {
                callResourceEntities
            }
        }


    override suspend fun upsertCallResources(callResourceEntities: List<CallResourceEntity>) {
        entitiesStateFlow.update { oldValues ->
            // New values come first so they overwrite old values
            (callResourceEntities + oldValues)
                .distinctBy(CallResourceEntity::id)
                .sortedWith(
                    compareBy(CallResourceEntity::startTime).reversed(),
                )
        }
    }

    override suspend fun getLastStartTime(): Long? =
        entitiesStateFlow.map {
            it.sortedWith(
                compareBy(CallResourceEntity::startTime).reversed(),
            ).firstOrNull()?.startTime
        }.first()


    override suspend fun deleteNewsResources(ids: List<String>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities ->
            entities.filterNot { it.id in idSet }
        }
    }
}