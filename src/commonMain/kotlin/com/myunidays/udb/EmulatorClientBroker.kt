package com.myunidays.udb

import com.myunidays.udb.adb.EmulatorClient
import com.myunidays.udb.adb.EmulatorProcessClient
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KProperty

class EmulatorClientBroker : ProcessExecutor {
    private val path: String by lazy {
        findAndroidTool(tool = "emulator")
    }

    private val client: EmulatorClient by lazy {
        EmulatorProcessClient(processExecutor = this)
    }

    override fun execCommand(command: String): Flow<String> {
        return bash(command = "$path ${command.trim()}")
    }

    operator fun getValue(container: Container, property: KProperty<*>): EmulatorClient = client
    operator fun getValue(nothing: Nothing?, property: KProperty<*>): EmulatorClient = client
    operator fun getValue(any: Any, property: KProperty<*>): EmulatorClient = client
}
