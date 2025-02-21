package com.nordsecurity.callmonitor.core.data.util

import android.content.Context
import android.net.ConnectivityManager
import com.nordsecurity.callmonitor.core.domain.IpAddressProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.Inet4Address
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultIpAddressProvider @Inject constructor(@ApplicationContext private val context: Context) :
    IpAddressProvider {
    override fun getLocalIpAddress(): String? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return null
        val network = connectivityManager.activeNetwork ?: return null
        val linkProperties = connectivityManager.getLinkProperties(network) ?: return null
        return linkProperties.linkAddresses
            .firstOrNull { linkAddress ->
                !linkAddress.address.isLoopbackAddress && linkAddress.address is Inet4Address
            }
            ?.address
            ?.hostAddress
    }
}

