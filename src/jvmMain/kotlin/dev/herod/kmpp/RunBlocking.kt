package dev.herod.kmpp

import kotlinx.coroutines.CoroutineScope

actual inline fun <T : Any> runBlocking(noinline block: suspend CoroutineScope.() -> T): T =
    kotlinx.coroutines.runBlocking(block = block)
