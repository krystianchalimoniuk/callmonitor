package com.nordsecurity.callmonitor.core.network.di

import com.nordsecurity.callmonitor.core.network.HttpServerHealthChecker
import com.nordsecurity.callmonitor.core.network.HttpServerManager
import com.nordsecurity.callmonitor.core.network.ktor.KtorHttpServerManager
import com.nordsecurity.callmonitor.core.network.ktor.KtorServerHealthChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {

    @Binds
    fun bindsHttpServerManager(impl: KtorHttpServerManager): HttpServerManager

    @Binds
    fun bindsHttpServerHealthChecker(impl: KtorServerHealthChecker): HttpServerHealthChecker


}


