package com.myunidays.udb.adb

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class UiAutomatorProcessClient(private val adbClient: AdbClient) : UiAutomatorClient {

    @FlowPreview
    override fun dump(): Flow<String> = flow {
        val dump = with(adbClient) {
            execCommand("exec-out uiautomator dump /dev/tty")
                .onEmpty {
                    val fallback =
                        execCommand("shell uiautomator dump")
                            .flatMapConcat {
                                execCommand("pull /sdcard/window_dump.xml")
                                    .flatMapConcat {
                                        execCommand("shell cat window_dump.xml")
                                    }
                            }
                    emitAll(fallback)
                }
                .filterNot { it.startsWith("ERROR:", ignoreCase = true) }
        }.toList()
            .joinToString("")
            .substringAfter('<')
            .substringBeforeLast('>')
            .let { "<$it>" }
        emit(dump)
    }
}
