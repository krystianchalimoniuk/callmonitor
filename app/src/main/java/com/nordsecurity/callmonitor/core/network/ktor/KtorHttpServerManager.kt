package com.nordsecurity.callmonitor.core.network.ktor

import android.util.Log
import com.nordsecurity.callmonitor.core.data.model.asEntity
import com.nordsecurity.callmonitor.core.data.model.asNetworkResource
import com.nordsecurity.callmonitor.core.domain.CallResourceRepository
import com.nordsecurity.callmonitor.core.network.HttpServerManager
import com.nordsecurity.callmonitor.core.domain.IpAddressProvider
import com.nordsecurity.callmonitor.core.domain.UserDataRepository
import com.nordsecurity.callmonitor.core.network.model.NetworkServerStatus
import com.nordsecurity.callmonitor.core.network.model.Service
import io.ktor.http.ContentType
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KtorHttpServerManager @Inject constructor(
    private val repository: CallResourceRepository,
    private val provider: IpAddressProvider,
    userDataRepository: UserDataRepository,
) :
    HttpServerManager {
    private var server:
            EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    private val port = 8080
    private val activeCallInfo = repository.observeActiveCall()
    private val userData = userDataRepository.userData
    override suspend fun startServer(): Boolean {
        val ip = "http://${provider.getLocalIpAddress()}:$port/"
        if (server == null) {
            server = embeddedServer(Netty, port = port) {
                routing {
                    get("/") {
                        call.respondText(
                            text = Json.encodeToString(
                                NetworkServerStatus(
                                    start = userData.first().serverStatus.startTime,
                                    arrayListOf(
                                        Service(name = "status", uri = "${ip}status"),
                                        Service(name = "log", uri = "${ip}log")
                                    )
                                )
                            ),
                            contentType = ContentType.Application.Json
                        )
                    }
                    get("/log") {
                        repository.refreshCallResources()
                        val recentCalls = repository.getCallResources(useNew = true).first()
                        call.respondText(
                            text = Json.encodeToString(recentCalls.map {
                                it.asNetworkResource()
                            }),
                            contentType = ContentType.Application.Json,
                        )
                        repository.incrementQueryCounterFor(recentCalls.map { it.asEntity() })
                    }
                    get("/status") {
                        try {
                            call.respondText(
                                text = Json.encodeToString(activeCallInfo.first()),
                                contentType = ContentType.Application.Json
                            )
                        } catch (e: Exception) {
                            Log.d("KtorHttpServerManager", e.toString())
                        }
                    }
                }
            }
            server?.start(wait = false)
        }
        return server != null
    }

    override suspend fun stopServer(): Boolean {
        server?.stop(1000, 5000)
        server = null
        return true
    }
}