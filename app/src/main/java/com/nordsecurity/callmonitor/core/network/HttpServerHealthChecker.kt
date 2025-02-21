package com.nordsecurity.callmonitor.core.network

interface HttpServerHealthChecker {
    suspend fun isServerAlive(): Boolean
}