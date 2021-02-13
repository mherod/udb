package com.myunidays.udb.adb

import com.myunidays.udb.adb.model.IUiNode
import com.myunidays.udb.adb.model.UiNode
import com.myunidays.udb.util.attributeBoolean
import com.myunidays.udb.util.attributeString
import kotlinx.coroutines.FlowPreview
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

inline fun <reified T> Flow<T>.stream(): Flow<T> {
    return flow {
        while (currentCoroutineContext().isActive) {
            emitAll(this@stream)
            delay(100)
        }
    }.distinctUntilChanged().conflate()
}

@FlowPreview
fun UiAutomatorClient.uiNodes(): Flow<IUiNode> {
    return dump()
        .onEmpty {
            error("dump was empty")
        }
        .retryWhen { _, _ ->
            delay(1_000)
            true
        }
        .flatMapConcat {
            "<[^>]+>".toRegex()
                .findAll(it)
                .flatMap(MatchResult::groupValues)
                .asFlow()
        }.map {
            UiNode(
                text = it attributeString "text",
                contentDescription = it attributeString "content-desc",
                resId = it attributeString "resource-id",
                pkg = it attributeString "package",
                clazz = it attributeString "class",
                boundsString = it attributeString "bounds",
                clickable = it attributeBoolean "clickable"
            )
        }
}
