package com.nordsecurity.callmonitor.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nordsecurity.callmonitor.core.database.dao.CallResourceDao
import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.database.model.asExternalModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class CallResourceDaoTest {

    private lateinit var callResourceDao: CallResourceDao
    private lateinit var db: CallMonitorDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            CallMonitorDatabase::class.java,
        ).build()
        callResourceDao = db.callResourceDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun callResourceDao_fetches_items_by_descending_publish_date() = runTest {

        callResourceDao.upsertCallResources(
            list,
        )

        val savedCallResourceEntities = callResourceDao.getCallResources()
            .first()

        kotlin.test.assertEquals(
            listOf(3L, 2L, 1L, 0L),
            savedCallResourceEntities.map {
                it.asExternalModel().startTime
            },
        )
    }


    @Test
    fun callResourceDao_deletes_items_by_ids() =
        runTest {
            callResourceDao.upsertCallResources(list)

            val (toDelete, toKeep) = list.partition { it.id.toInt() % 2 == 0 }

            callResourceDao.deleteNewsResources(
                toDelete.map(CallResourceEntity::id),
            )

            kotlin.test.assertEquals(
                toKeep.map(CallResourceEntity::id)
                    .toSet(),
                callResourceDao.getCallResources().first()
                    .map { it.id }
                    .toSet(),
            )
        }
}

private val list = listOf(
    CallResourceEntity(
        id = "1",
        timesQueried = 0,
        callerName = "John Doe",
        duration = 20,
        phoneNumber = "+48 233424123",
        startTime = 0L
    ),
    CallResourceEntity(
        id = "2",
        timesQueried = 1,
        callerName = "Bob Doe",
        duration = 30,
        phoneNumber = "+48 235424123",
        startTime = 3L,
    ),
    CallResourceEntity(
        id = "3",
        timesQueried = 1,
        callerName = "Alice Doe",
        duration = 30,
        phoneNumber = "+48 235424123",
        startTime = 1L,
    ),
    CallResourceEntity(
        id = "4",
        timesQueried = 1,
        callerName = "Ann Doe",
        duration = 30,
        phoneNumber = "+48 235424123",
        startTime = 2L,
    ),

    )