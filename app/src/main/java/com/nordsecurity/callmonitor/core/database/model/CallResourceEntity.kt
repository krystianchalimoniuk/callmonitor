package com.nordsecurity.callmonitor.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nordsecurity.callmonitor.core.model.CallResource

@Entity(tableName = "call_resources")
data class CallResourceEntity(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "caller_name")
    var callerName: String?,
    @ColumnInfo(name = "phone_number")
    var phoneNumber: String,
    var duration: Int,
    @ColumnInfo(name = "start_time")
    var startTime: Long,
    @ColumnInfo(name = "times_queried")
    val timesQueried: Int = 0,
    @ColumnInfo(name = "is_new")
    val isNew: Boolean = true
)

fun CallResourceEntity.asExternalModel() = CallResource(
    id = id,
    callerName = callerName,
    phoneNumber = phoneNumber,
    duration = duration,
    startTime = startTime,
    timesQueried = timesQueried
)