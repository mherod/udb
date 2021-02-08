package com.myunidays.udb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

actual fun bash(command: String): Flow<String> = flow {
    val bashCommand = "bash -c \"$command\""
    println("BASH: $bashCommand")
    val bash = exec(bashCommand)
    emitAll(bash)
}
