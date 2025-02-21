package com.nordsecurity.callmonitor.core.data.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.nordsecurity.callmonitor.core.domain.ServerServiceController
import com.nordsecurity.callmonitor.core.service.CallMonitorService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultServerServiceController @Inject constructor(@ApplicationContext private val context: Context) :
    ServerServiceController {
    override fun startService() {
        val intent = Intent(context, CallMonitorService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopService() {
        val intent = Intent(context, CallMonitorService::class.java)
        context.stopService(intent)
    }

}