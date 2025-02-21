package com.nordsecurity.callmonitor.core.testing.rules

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_PHONE_STATE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.test.rule.GrantPermissionRule.grant
import org.junit.rules.TestRule

/**
 * [TestRule] granting [READ_PHONE_STATE] permission
 */
class GrantReadPhoneStatePermissionRule :
    TestRule by grant(READ_PHONE_STATE)
