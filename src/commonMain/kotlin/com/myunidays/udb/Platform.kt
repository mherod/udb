package com.myunidays.udb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

expect inline fun mainBlock(crossinline block: suspend CoroutineScope.() -> Unit)

expect inline fun <T : Any> runBlocking(noinline block: suspend CoroutineScope.() -> T): T

expect fun exec(command: String): Flow<String>

expect fun envVar(key: String): String?

expect fun findFile(searchPath: String, fileName: String): Flow<String>
