package com.nordsecurity.callmonitor.core.data.model

import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.model.CallResource
import com.nordsecurity.callmonitor.core.network.model.NetworkCallResource
import kotlinx.datetime.Instant

fun CallResource.asNetworkResource() = NetworkCallResource(
    beginning = Instant.fromEpochMilliseconds(startTime)
        .toString(),
    duration = duration.toString(),
    number = phoneNumber,
    name = callerName ?: "-",
    timesQueried = timesQueried
)

fun CallResource.asEntity() = CallResourceEntity(
    id = id,
    startTime = startTime,
    duration = duration,
    phoneNumber = phoneNumber,
    callerName = callerName,
    timesQueried = timesQueried

)