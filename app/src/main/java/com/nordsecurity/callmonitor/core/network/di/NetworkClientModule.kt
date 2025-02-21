package com.nordsecurity.callmonitor.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkClientModule {
    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 3000
                connectTimeoutMillis = 3000
                socketTimeoutMillis = 3000
            }
            defaultRequest {
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }
        }
    }
}
