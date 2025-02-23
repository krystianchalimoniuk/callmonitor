package com.nordsecurity.callmonitor.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkError(
   @SerialName("error")
    val error: String,
    val code: Int
)