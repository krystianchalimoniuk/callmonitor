package com.nordsecurity.callmonitor.core.testing.repository

import com.nordsecurity.callmonitor.core.domain.UserDataRepository
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.model.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull


val emptyUserData = UserData(
    serverStatus = ServerStatus(startTime = "2025-02-21T16:46:44.352Z", isRunning = true)
)

class TestUserDataRepository : UserDataRepository {
    /**
     * The backing hot flow for the list of followed topic ids for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()

    override suspend fun setServerStatus(serverStatus: ServerStatus) {
        _userData.tryEmit(currentUserData.copy(serverStatus = serverStatus))
    }
}