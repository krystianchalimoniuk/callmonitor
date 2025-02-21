package com.nordsecurity.callmonitor.sync.workers

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nordsecurity.callmonitor.core.analytics.AnalyticsHelper
import com.nordsecurity.callmonitor.core.common.network.CallMonitorDispatchers
import com.nordsecurity.callmonitor.core.common.network.Dispatcher
import com.nordsecurity.callmonitor.core.data.model.asEntity
import com.nordsecurity.callmonitor.core.domain.CallResourceRepository
import dagger.assisted.Assisted
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import android.Manifest
import android.content.pm.PackageManager
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import com.nordsecurity.callmonitor.core.domain.UserDataRepository
import com.nordsecurity.callmonitor.core.model.ServerStatus
import com.nordsecurity.callmonitor.core.network.HttpServerHealthChecker
import com.nordsecurity.callmonitor.sync.initializers.SyncConstraints
import com.nordsecurity.callmonitor.sync.initializers.syncForegroundInfo
import dagger.assisted.AssistedInject
import java.time.Instant

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userDataRepository: UserDataRepository,
    private val callResourceRepository: CallResourceRepository,
    private val httpServerHealthChecker: HttpServerHealthChecker,
    @Dispatcher(CallMonitorDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        analyticsHelper.logSyncStarted()
        val calls = callResourceRepository.getCallResources(useNew = true).first()
        if (calls.isNotEmpty()) {
            callResourceRepository.markAsViewed(calls.map { it.asEntity() })
        }
        val permissionStatus = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.READ_CALL_LOG
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            callResourceRepository.refreshCallResources()
        }
        if (userDataRepository.userData.first().serverStatus.isRunning) {
            if (!httpServerHealthChecker.isServerAlive()) {
                userDataRepository.setServerStatus(
                    ServerStatus(
                        startTime = Instant.now().toString(),
                        isRunning = false
                    )
                )
            }
        }
        analyticsHelper.logSyncFinished(true)
        Result.success()
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}