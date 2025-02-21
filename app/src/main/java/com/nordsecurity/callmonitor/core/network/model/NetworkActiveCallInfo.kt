package com.nordsecurity.callmonitor.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkActiveCallInfo(
    @SerialName("ongoing")
    val ongoing: Boolean,
    @SerialName("number")
    val number: String,
    @SerialName("name")
    val name: String,
)