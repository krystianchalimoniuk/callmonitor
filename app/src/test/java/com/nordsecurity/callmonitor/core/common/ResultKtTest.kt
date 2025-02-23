package com.nordsecurity.callmonitor.core.common

import app.cash.turbine.test
import com.nordsecurity.callmonitor.core.common.result.asResult
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import com.nordsecurity.callmonitor.core.common.result.Result

class ResultKtTest {

    @Test
    fun Result_catches_errors() = runTest {
        flow {
            emit(1)
            throw Exception("Test Done")
        }
            .asResult()
            .test {
                assertEquals(Result.Loading, awaitItem())
                assertEquals(Result.Success(1), awaitItem())

                when (val errorResult = awaitItem()) {
                    is Result.Error -> assertEquals(
                        "Test Done",
                        errorResult.exception?.message,
                    )

                    Result.Loading,
                    is Result.Success,
                        -> throw IllegalStateException(
                        "The flow should have emitted an Error Result",
                    )
                }

                awaitComplete()
            }
    }
}
