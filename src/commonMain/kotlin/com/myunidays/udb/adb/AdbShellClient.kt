package com.myunidays.udb.adb

import kotlinx.coroutines.flow.Flow

interface AdbShellClient {
    fun execCommand(command: String): Flow<String>
}
