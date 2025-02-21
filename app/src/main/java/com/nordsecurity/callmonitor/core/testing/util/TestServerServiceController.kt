package com.nordsecurity.callmonitor.core.testing.util

import com.nordsecurity.callmonitor.core.domain.ServerServiceController

class TestServerServiceController : ServerServiceController {
    override fun startService() = Unit

    override fun stopService() = Unit

}