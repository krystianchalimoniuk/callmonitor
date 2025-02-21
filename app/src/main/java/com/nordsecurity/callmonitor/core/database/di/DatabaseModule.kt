package com.nordsecurity.callmonitor.core.database.di

import android.content.Context
import androidx.room.Room
import com.nordsecurity.callmonitor.core.database.CallMonitorDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): CallMonitorDatabase = Room.databaseBuilder(
        context,
        CallMonitorDatabase::class.java,
        "call-monitor-database",
    ).build()
}
