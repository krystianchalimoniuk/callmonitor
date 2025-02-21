package com.nordsecurity.callmonitor.core.datastore

import androidx.datastore.core.DataStore
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CallMonitorPreferenceDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                serverStatus = ServerStatus(
                    startTime = it.serverStatus.startTime,
                    isRunning = it.serverStatus.isRunning
                )
            )
        }

    suspend fun setServerStatus(serverStatus: ServerStatus) {
        userPreferences.updateData {
            it.copy {
                this.serverStatus = it.serverStatus.copy {
                    this.startTime = serverStatus.startTime
                    this.isRunning = serverStatus.isRunning
                }
            }
        }
    }
}