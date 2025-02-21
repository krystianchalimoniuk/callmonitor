package com.nordsecurity.callmonitor.core.domain

interface IpAddressProvider {
    fun getLocalIpAddress(): String?
}