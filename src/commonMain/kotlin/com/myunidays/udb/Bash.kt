package com.myunidays.udb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun bash(command: String): Flow<String> = flow {
    val flow = exec(command = "bash -c \"$command\"")
    emitAll(flow)
}
