package com.myunidays.udb.adb

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive

interface UiAutomatorClient {
    fun dump(): Flow<String>
}

fun UiAutomatorClient.stream(): Flow<String> {
    return flow {
        while (currentCoroutineContext().isActive) {
            emitAll(dump())
            delay(100)
        }
    }.distinctUntilChanged().conflate()
}
