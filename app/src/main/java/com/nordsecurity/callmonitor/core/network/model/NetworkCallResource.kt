package com.nordsecurity.callmonitor.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCallResource(
    @SerialName("beginning")
    val beginning: String,
    @SerialName("duration")
    val duration: String,
    @SerialName("number")
    val number: String,
    @SerialName("name")
    val name: String,
    @SerialName("timesQueried")
    val timesQueried: Int,
)