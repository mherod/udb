package com.myunidays.udb.adb

import dev.herod.kmpp.exec
import kotlinx.coroutines.flow.Flow

class AdbShellSingleClient : AdbShellClient {
    override fun execCommand(command: String): Flow<String> {
        return exec("adb shell $command")
    }
}
