package com.nordsecurity.callmonitor.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkServerStatus(
    @SerialName("start")
    val start: String,
    @SerialName("services")
    val services: List<Service>

)

@Serializable
data class Service(
    @SerialName("name")
    val name: String,
    @SerialName("uri")
    val uri: String
)