package com.myunidays.udb.adb

import com.myunidays.udb.ProcessExecutor
import kotlinx.coroutines.flow.Flow

class EmulatorProcessClient(
    private val processExecutor: ProcessExecutor,
) : EmulatorClient {

    override fun listAvds(): Flow<String> {
        return processExecutor.execCommand(" -list-avds")
    }

    override fun launch(avd: String): Flow<String> {
        return processExecutor.execCommand(" -avd $avd")
    }
}
