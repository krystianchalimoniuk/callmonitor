package com.nordsecurity.callmonitor.core.model


data class CallResource(
    val id: String,
    val callerName: String?,
    val phoneNumber: String,
    val duration: Int,
    val startTime: Long,
    val timesQueried: Int
)

