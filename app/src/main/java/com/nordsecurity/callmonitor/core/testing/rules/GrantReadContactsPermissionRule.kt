package com.nordsecurity.callmonitor.core.testing.rules

import android.Manifest.permission.READ_CONTACTS
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.test.rule.GrantPermissionRule.grant
import org.junit.rules.TestRule

/**
 * [TestRule] granting [READ_CONTACTS] permission if running on [SDK_INT] lower than [VERSION_CODES.S].
 */
class GrantReadContactsPermissionRule :
    TestRule by if (SDK_INT < VERSION_CODES.S) grant(READ_CONTACTS) else grant()
