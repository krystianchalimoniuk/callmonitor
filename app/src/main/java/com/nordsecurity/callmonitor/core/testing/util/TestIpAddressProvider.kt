package com.nordsecurity.callmonitor.core.testing.util

import com.nordsecurity.callmonitor.core.domain.IpAddressProvider

class TestIpAddressProvider : IpAddressProvider {
    override fun getLocalIpAddress(): String? {
        return "192.168.0.1"
    }
}