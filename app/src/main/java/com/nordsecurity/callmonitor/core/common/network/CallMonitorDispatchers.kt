package com.nordsecurity.callmonitor.core.common.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val callMonitorDispatcher: CallMonitorDispatchers)

enum class CallMonitorDispatchers {
    Default,
    IO,
}
