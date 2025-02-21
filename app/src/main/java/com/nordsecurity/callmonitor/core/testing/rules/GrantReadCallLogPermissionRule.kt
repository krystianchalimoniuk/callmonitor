package com.nordsecurity.callmonitor.core.testing.rules

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_CALL_LOG
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.test.rule.GrantPermissionRule.grant
import org.junit.rules.TestRule

/**
 * [TestRule] granting [READ_CALL_LOG] permission
 */
class GrantReadCallLogPermissionRule :
    TestRule by grant(READ_CALL_LOG)
