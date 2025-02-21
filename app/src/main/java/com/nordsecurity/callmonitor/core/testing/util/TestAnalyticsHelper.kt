package com.nordsecurity.callmonitor.core.testing.util

import com.nordsecurity.callmonitor.core.analytics.AnalyticsEvent
import com.nordsecurity.callmonitor.core.analytics.AnalyticsHelper

class TestAnalyticsHelper : AnalyticsHelper {

    private val events = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    fun hasLogged(event: AnalyticsEvent) = event in events
}
