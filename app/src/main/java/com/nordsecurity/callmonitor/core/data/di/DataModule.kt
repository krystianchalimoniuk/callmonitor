package com.nordsecurity.callmonitor.core.data.di

import com.nordsecurity.callmonitor.core.data.util.DefaultServerServiceController
import com.nordsecurity.callmonitor.core.data.util.ConnectivityManagerNetworkMonitor
import com.nordsecurity.callmonitor.core.data.repositories.DefaultCallLogLocalDataSource
import com.nordsecurity.callmonitor.core.data.repositories.DefaultCallResourceRepository
import com.nordsecurity.callmonitor.core.data.repositories.DefaultUserDataRepository
import com.nordsecurity.callmonitor.core.data.util.DefaultIpAddressProvider
import com.nordsecurity.callmonitor.core.data.util.TimeZoneBroadcastMonitor
import com.nordsecurity.callmonitor.core.domain.CallLogLocalDataSource
import com.nordsecurity.callmonitor.core.domain.CallResourceRepository
import com.nordsecurity.callmonitor.core.domain.IpAddressProvider
import com.nordsecurity.callmonitor.core.domain.NetworkMonitor
import com.nordsecurity.callmonitor.core.domain.ServerServiceController
import com.nordsecurity.callmonitor.core.domain.TimeZoneMonitor
import com.nordsecurity.callmonitor.core.domain.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsIpAddressProvider(
        ipAddressProvider: DefaultIpAddressProvider
    ): IpAddressProvider

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsCallLogLocalDataSource(
        callLogRepository: DefaultCallLogLocalDataSource
    ): CallLogLocalDataSource

    @Binds
    internal abstract fun bindsCallResourceRepository(
        callResourceRepository: DefaultCallResourceRepository
    ): CallResourceRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: DefaultUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindServerServiceController(
        serverServiceController: DefaultServerServiceController
    ): ServerServiceController

    @Binds
    internal abstract fun bindsTimeZoneMonitor(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
