package com.myunidays.udb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

actual fun bash(command: String): Flow<String> = flow {
    val escaped = command
        .replace("\\$(\\S+)".toRegex()) { result ->
            val key = result.groupValues.last()
            envVar(key).orEmpty()
        }
        .replace("\n".toRegex()) { "; " }
        .replace("\"".toRegex()) { "\\\"" }
        .let { line ->
            "\"${line.trim()}\""
        }
    println("!! $escaped")
    val output = ProcessBuilder()
        .command("bash", "-c", escaped)
        .start()
        .mergedInputStreamFlow()
    emitAll(output)
}
