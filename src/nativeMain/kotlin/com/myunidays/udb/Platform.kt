package com.myunidays.udb

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import platform.posix.*

val debugLogsEnabled: Boolean by lazy(getenv("DEBUG")?.toKString()::toBoolean)

actual inline fun mainBlock(crossinline block: suspend CoroutineScope.() -> Unit): Unit = runBlocking {
    runCatching { block() }
        .onFailure { throwable ->
            println("fatal: ${throwable.message?.substringAfter("fatal:")?.trim()}")
            if (debugLogsEnabled) {
                throwable.printStackTrace()
            }
        }
        .onFailure { exit(1) }
        .onSuccess { exit(0) }
}

actual fun exec(command: String): Flow<String> = flow {
    if (debugLogsEnabled) println(">> $command")
    popen(command, "r")?.let { pointer ->
        memScoped {
            val readBufferLength = 128
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, pointer)?.toKString()?.trim()
            while (line != null) {
                if (debugLogsEnabled) println("<< $line")
                emit(line)
                line = fgets(buffer, readBufferLength, pointer)?.toKString()?.trim()
            }
        }
        pclose(pointer)
    }
}

actual fun envVar(key: String): String? = getenv(key)?.toKString()?.takeUnless { it.isBlank() }
