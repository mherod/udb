package com.myunidays.udb.util

import com.myunidays.udb.cli.ChangePair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.changes(): Flow<ChangePair<T>> = flow {
    var last: T? = null
    distinctUntilChanged().collect { value ->
        last?.let { last1 ->
            emit(ChangePair(last1 to value))
        }
        last = value
    }
}
