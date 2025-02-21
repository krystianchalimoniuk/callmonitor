package com.nordsecurity.callmonitor.app

import android.app.Application
import com.nordsecurity.callmonitor.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

/**
 * [Application] class for Call Monitor
 */
@HiltAndroidApp
class CallMonitorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Sync; the system responsible for keeping data in the app up to date.
        Sync.initialize(context = this)

    }
}