package com.nordsecurity.callmonitor.core.data.model

import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.model.CallResource
import com.nordsecurity.callmonitor.core.network.model.NetworkCallResource
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class NetworkExternalEntityTest {

    @Test
    fun callResourceMapsToNetworkModel() {
        val callResource = CallResource(
            id = "1",
            callerName = "John Doe",
            phoneNumber = "+48 233424123",
            duration = 20,
            startTime = 1739965430L,
            timesQueried = 0
        )

        val networkCallResource = callResource.asNetworkResource()
        val expected = NetworkCallResource(
            timesQueried = 0,
            name = "John Doe",
            duration = "20",
            number = "+48 233424123",
            beginning = Instant.fromEpochMilliseconds(1739965430L).toString()
        )
        assertEquals(expected, networkCallResource)
    }

    @Test
    fun callResourceMapsToDatabaseModel() {
        val callResource = CallResource(
            id = "1",
            callerName = "John Doe",
            phoneNumber = "+48 233424123",
            duration = 20,
            startTime = 1739965430L,
            timesQueried = 0
        )

        val networkCallResource = callResource.asEntity()
        val expected = CallResourceEntity(
            id = "1",
            timesQueried = 0,
            callerName = "John Doe",
            duration = 20,
            phoneNumber = "+48 233424123",
            startTime = 1739965430L
        )
        assertEquals(expected, networkCallResource)
    }
}