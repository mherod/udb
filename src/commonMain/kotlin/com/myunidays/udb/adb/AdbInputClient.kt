package com.myunidays.udb.adb

import com.myunidays.udb.adb.model.IUiNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface AdbInputClient {
    fun tap(x: Int, y: Int): Flow<String>
    fun text(text: String): Flow<String>
}

fun AdbInputClient.tap(node: IUiNode): Flow<String> = flow {
    val bounds = node.bounds
    emitAll(
        flow = tap(
            x = bounds.centreX,
            y = bounds.centreY
        )
    )
}
