package com.nordsecurity.callmonitor.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nordsecurity.callmonitor.core.database.dao.CallResourceDao
import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.database.util.InstantConverter

@Database(
    entities = [
        CallResourceEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class CallMonitorDatabase : RoomDatabase() {
    abstract fun callResourceDao(): CallResourceDao
}
