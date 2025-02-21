package com.nordsecurity.callmonitor.core.database.di

import com.nordsecurity.callmonitor.core.database.CallMonitorDatabase
import com.nordsecurity.callmonitor.core.database.dao.CallResourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesNewsResourceDao(
        database: CallMonitorDatabase,
    ): CallResourceDao = database.callResourceDao()

}
