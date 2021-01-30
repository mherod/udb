@file:Suppress("NOTHING_TO_INLINE")

package com.myunidays.udb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlin.system.exitProcess

actual inline fun mainBlock(crossinline block: suspend CoroutineScope.() -> Unit) {
    kotlinx.coroutines.runBlocking {
        runCatching { block() }
            .onFailure { throwable ->
                val message = throwable.message ?: throwable.cause?.message
                println("fatal: ${message?.substringAfter("fatal:")?.trim()}")
                throwable.printStackTrace()
            }
            .onFailure { exitProcess(1) }
            .onSuccess { exitProcess(0) }
    }
    Unit
}

actual fun exec(command: String): Flow<String> {
    return Runtime.getRuntime()
        .exec(command)
        .inputStream
        .bufferedReader()
        .lineSequence()
        .asFlow()
}

actual fun envVar(key: String): String? = System.getenv(key)?.takeUnless { it.isBlank() }
