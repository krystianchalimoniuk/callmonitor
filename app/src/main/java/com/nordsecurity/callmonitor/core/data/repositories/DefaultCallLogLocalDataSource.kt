package com.nordsecurity.callmonitor.core.data.repositories

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import com.nordsecurity.callmonitor.core.common.network.CallMonitorDispatchers
import com.nordsecurity.callmonitor.core.common.network.Dispatcher
import com.nordsecurity.callmonitor.core.database.model.CallResourceEntity
import com.nordsecurity.callmonitor.core.domain.CallLogLocalDataSource
import com.nordsecurity.callmonitor.core.model.ActiveCallInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DefaultCallLogLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(
        CallMonitorDispatchers.IO
    ) private val ioDispatcher: CoroutineDispatcher,
) : CallLogLocalDataSource {
    override suspend fun getRecentCalls(lastStartTime: Long?): List<CallResourceEntity> {
        val calls = mutableListOf<CallResourceEntity>()
        val selection: String? = if (lastStartTime != null) "${CallLog.Calls.DATE} > ?" else null
        val selectionArgs: Array<String>? =
            if (lastStartTime != null) arrayOf(lastStartTime.toString()) else null

        val cursor: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            "${CallLog.Calls.DATE} DESC"
        )
        cursor?.use {
            val idIndex = it.getColumnIndex(CallLog.Calls._ID)
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                val duration = it.getInt(durationIndex)
                val date = it.getLong(dateIndex)
                calls.add(CallResourceEntity(id, name, number, duration, date, timesQueried = 0))
            }
        }
        return calls.take(50)
    }


    override fun observeActiveCall(): Flow<ActiveCallInfo?> = callbackFlow {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val callback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    when (state) {
                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            trySend(
                                ActiveCallInfo(
                                    ongoing = true,
                                    phoneNumber = "-",
                                    callerName = "-"
                                )
                            )

                        }

                        TelephonyManager.CALL_STATE_IDLE -> {
                            trySend(
                                ActiveCallInfo(
                                    ongoing = false,
                                    phoneNumber = "-",
                                    callerName = "-"
                                )
                            )
                        }

                        TelephonyManager.CALL_STATE_RINGING -> {
                            trySend(
                                ActiveCallInfo(
                                    ongoing = true,
                                    phoneNumber = "-",
                                    callerName = "-"
                                )
                            )
                        }
                    }
                }
            }
            telephonyManager.registerTelephonyCallback(context.mainExecutor, callback)
            awaitClose { telephonyManager.unregisterTelephonyCallback(callback) }
        } else {
            val listener = object : PhoneStateListener() {
                @Deprecated("Deprecated in Java")
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state) {
                        TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> {
                            val contactName = getContactNameFromNumber(context, phoneNumber)
                            trySend(
                                ActiveCallInfo(
                                    ongoing = true,
                                    phoneNumber = phoneNumber ?: "-",
                                    callerName = contactName ?: "-"
                                )
                            )

                        }

                        TelephonyManager.CALL_STATE_IDLE -> {
                            trySend(
                                ActiveCallInfo(
                                    ongoing = false,
                                    phoneNumber = "-",
                                    callerName = "-"
                                )
                            )
                        }


                    }
                }
            }
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
            awaitClose { telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE) }

        }
    }.flowOn(ioDispatcher)
        .conflate()

    override fun observeCallLogChanges(): Flow<Unit> = callbackFlow {
        val handler = Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                trySend(Unit)
            }
        }
        context.contentResolver.registerContentObserver(
            CallLog.Calls.CONTENT_URI,
            true,
            observer
        )
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(ioDispatcher).conflate()


    private fun getContactNameFromNumber(context: Context, phoneNumber: String?): String? {
        if (phoneNumber.isNullOrBlank()) return null

        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                return cursor.getString(nameIndex)
            }
        }
        return null
    }
}