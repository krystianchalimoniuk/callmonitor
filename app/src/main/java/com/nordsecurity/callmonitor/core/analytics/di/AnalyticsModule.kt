package com.nordsecurity.callmonitor.core.analytics.di

import com.nordsecurity.callmonitor.core.analytics.AnalyticsHelper
import com.nordsecurity.callmonitor.core.analytics.StubAnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {
    @Binds
    abstract fun bindsAnalyticsHelper(analyticsHelperImpl: StubAnalyticsHelper): AnalyticsHelper
}
