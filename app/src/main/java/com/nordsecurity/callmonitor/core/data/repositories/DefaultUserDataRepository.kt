package com.nordsecurity.callmonitor.core.data.repositories

import com.nordsecurity.callmonitor.core.datastore.CallMonitorPreferenceDataSource
import com.nordsecurity.callmonitor.core.domain.UserDataRepository
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultUserDataRepository @Inject constructor(private val callMonitorPreferenceDataSource: CallMonitorPreferenceDataSource) :
    UserDataRepository {
    override val userData: Flow<UserData>
        get() = callMonitorPreferenceDataSource.userData

    override suspend fun setServerStatus(serverStatus: ServerStatus) {
        callMonitorPreferenceDataSource.setServerStatus(serverStatus)
    }
}
