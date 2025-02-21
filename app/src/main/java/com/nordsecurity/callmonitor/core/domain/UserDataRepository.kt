package com.nordsecurity.callmonitor.core.domain

import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets the user's serverStatus
     */
    suspend fun setServerStatus(serverStatus: ServerStatus)
}