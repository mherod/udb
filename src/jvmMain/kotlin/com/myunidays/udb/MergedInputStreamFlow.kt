package com.myunidays.udb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.merge

fun Process.mergedInputStreamFlow(): Flow<String> {
    val inputStreamFlow = inputStream
        .bufferedReader()
        .lineSequence()
        .asFlow()
    val errorStreamFlow = errorStream
        .bufferedReader()
        .lineSequence()
        .asFlow()
    return merge(inputStreamFlow, errorStreamFlow)
}
