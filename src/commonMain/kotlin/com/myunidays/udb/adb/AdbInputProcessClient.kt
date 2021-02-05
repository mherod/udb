package com.myunidays.udb.adb

import kotlinx.coroutines.flow.Flow

class AdbInputProcessClient(private val adbClient: AdbClient) : AdbInputClient {

    override fun tap(x: Int, y: Int): Flow<String> {
        return adbClient.execCommand("shell input tap $x $y")
    }

    override fun text(text: String): Flow<String> {
        return adbClient.execCommand("shell input text $text")
    }
}
