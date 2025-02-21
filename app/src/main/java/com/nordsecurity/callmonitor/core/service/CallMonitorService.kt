package com.nordsecurity.callmonitor.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nordsecurity.callmonitor.R
import com.nordsecurity.callmonitor.app.MainActivity
import com.nordsecurity.callmonitor.core.common.network.CallMonitorDispatchers
import com.nordsecurity.callmonitor.core.common.network.Dispatcher
import com.nordsecurity.callmonitor.core.common.network.di.ApplicationScope
import com.nordsecurity.callmonitor.core.network.HttpServerManager
import com.nordsecurity.callmonitor.core.domain.IpAddressProvider
import com.nordsecurity.callmonitor.core.domain.UserDataRepository
import com.nordsecurity.callmonitor.core.model.ServerStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class CallMonitorService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "CallMonitorChannel"
        var isServiceRunning = false
    }

    @Inject
    lateinit var httpServerManager: HttpServerManager

    @Inject
    @Dispatcher(CallMonitorDispatchers.IO)
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    lateinit var ipAddressProvider: IpAddressProvider

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        applicationScope.launch(ioDispatcher) {
            userDataRepository.setServerStatus(
                ServerStatus(
                    startTime = Instant.now().toString(), isRunning = true
                )
            )
            httpServerManager.startServer()
        }
        val notification = buildNotification()
        startForeground(1, notification)
        return START_STICKY
    }


    override fun onDestroy() {
        applicationScope.launch(ioDispatcher) {
            userDataRepository.setServerStatus(
                ServerStatus(
                    startTime = Instant.now().toString(), isRunning = false
                )
            )
            httpServerManager.stopServer()
            isServiceRunning = false
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val notificationIntent =
            Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Call Monitor Running")
            .setContentText("HTTP Server: ${ipAddressProvider.getLocalIpAddress()}:8080")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Call Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}