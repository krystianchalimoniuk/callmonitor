package com.nordsecurity.callmonitor.core.domain

import kotlinx.coroutines.flow.Flow

interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun requestSync()
}
