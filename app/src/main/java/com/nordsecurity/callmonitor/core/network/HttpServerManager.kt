package com.nordsecurity.callmonitor.core.network

interface HttpServerManager {
    suspend fun startServer(): Boolean
    suspend fun stopServer(): Boolean
}