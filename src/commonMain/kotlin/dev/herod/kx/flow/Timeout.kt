package dev.herod.kx.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

fun <T> Flow<T>.timeout(timeoutDelay: Long): Flow<T> {
    val upstreamFlow = this
    return flow {
        withTimeout(timeoutDelay) {
            upstreamFlow.collect { value ->
                emit(value)
            }
        }
    }
}
