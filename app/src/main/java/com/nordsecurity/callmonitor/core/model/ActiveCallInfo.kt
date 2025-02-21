package com.nordsecurity.callmonitor.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ActiveCallInfo(
    val ongoing: Boolean,
    val phoneNumber: String,
    val callerName: String? = null
)