package com.myunidays.udb.adb

import com.myunidays.udb.util.extractGroup
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class UiAutomatorProcessClient(private val adbClient: AdbClient) : UiAutomatorClient {

    @FlowPreview
    override fun dump(): Flow<String> = flow {

        val joinedRaw = with(adbClient) {
            emptyFlow<String>()
//            execCommand("exec-out uiautomator dump /dev/tty")
                .onEmpty {
                    val fallback =
                        execCommand("shell uiautomator dump")
                            .flatMapConcat {
                                val fileName = "(\\S+\\.xml)".toRegex().extractGroup(it)
                                execCommand("shell cat $fileName")
                            }
                    emitAll(fallback)
                }
                .filterNot { it.startsWith("ERROR:", ignoreCase = true) }
        }.toList().joinToString("")

        require(joinedRaw.isNotBlank())

        val dump = joinedRaw
            .substringAfter('<')
            .substringBeforeLast('>')
            .let { "<$it>" }

        emit(dump)
    }
}
