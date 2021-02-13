@file:Suppress("NOTHING_TO_INLINE")

package com.myunidays.udb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

actual inline fun mainBlock(crossinline block: suspend CoroutineScope.() -> Unit) {
    runBlocking {
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
//    println(command)
    return Runtime.getRuntime().exec(command).mergedInputStreamFlow()
}

actual fun envVar(key: String): String? = System.getenv(key)?.takeUnless { it.isBlank() }

actual fun findFile(searchPath: String, fileName: String): Flow<String> {
    return File(searchPath)
        .walkTopDown()
        .filter { it.isFile && it.name == fileName }
        .map { it.absolutePath }
        .asFlow()
}
