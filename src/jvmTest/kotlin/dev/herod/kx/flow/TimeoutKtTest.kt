package dev.herod.kx.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertTrue

class TimeoutKtTest {

    @Test
    fun `did timeout test`() = runBlocking {
        var good = false
        flow { delay(1_000); emit(Unit) }
            .timeout(500)
            .catch {
                good = true
            }
            .collect()
        assertTrue(actual = good)
    }

    @Test
    fun `didn't timeout out test`() = runBlocking {
        var good = true
        flow { delay(500); emit(Unit) }
            .timeout(1_000)
            .catch {
                good = false
            }
            .collect()
        assertTrue(actual = good)
    }
}