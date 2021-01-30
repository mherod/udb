package com.myunidays.udb.adb

import com.myunidays.udb.exec
import kotlinx.coroutines.flow.Flow

class AdbShellSingleClient : AdbShellClient {
    override fun execCommand(command: String): Flow<String> {
        return exec("adb shell $command")
    }
}
