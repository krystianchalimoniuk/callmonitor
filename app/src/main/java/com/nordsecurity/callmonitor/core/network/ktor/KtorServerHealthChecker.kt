package com.nordsecurity.callmonitor.core.network.ktor

import com.nordsecurity.callmonitor.core.common.network.CallMonitorDispatchers
import com.nordsecurity.callmonitor.core.common.network.Dispatcher
import com.nordsecurity.callmonitor.core.domain.IpAddressProvider
import com.nordsecurity.callmonitor.core.network.HttpServerHealthChecker
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KtorServerHealthChecker @Inject constructor(
    @Dispatcher(CallMonitorDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val client: HttpClient,
    private val ipAddressProvider: IpAddressProvider
) : HttpServerHealthChecker {
    override suspend fun isServerAlive(): Boolean = withContext(ioDispatcher) {
        return@withContext try {
            val response = client.get("http://${ipAddressProvider.getLocalIpAddress()}:8080/")
            client.close()
            response.toString().contains("true", ignoreCase = true)
        } catch (e: Exception) {
            client.close()
            false
        }
    }

}