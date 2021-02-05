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

@FlowPreview
fun UiAutomatorClient.uiNodes(): Flow<IUiNode> {
    return dump()
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
