package com.nordsecurity.callmonitor.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallResourceDao {


    /**
     * Fetches news resources that match the query parameters
     */
    @Transaction
    @Query(
        """
    SELECT * FROM call_resources
    WHERE (:useNew = 0 OR is_new = 1)
    ORDER BY start_time DESC
"""
    )
    fun getCallResources(useNew: Boolean = false): Flow<List<CallResourceEntity>>

    /**
     * Inserts or updates [callResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertCallResources(callResourceEntities: List<CallResourceEntity>)

    @Query("SELECT MAX(start_time) FROM call_resources")
    suspend fun getLastStartTime(): Long?

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(
        value = """
            DELETE FROM call_resources
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteNewsResources(ids: List<String>)
}