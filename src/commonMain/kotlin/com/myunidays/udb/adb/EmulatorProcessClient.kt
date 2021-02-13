package com.myunidays.udb.adb

import com.myunidays.udb.ProcessExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot

class EmulatorProcessClient(
    private val processExecutor: ProcessExecutor,
) : EmulatorClient {

    override fun listAvds(): Flow<String> {
        return processExecutor.execCommand(command = " -list-avds")
            .filterNot { "bash: " in it }
    }

    override fun launch(
        avd: String,
        quiet: Boolean,
        noWindow: Boolean,
        noAudio: Boolean,
        noBootAnim: Boolean,
    ): Flow<String> {
        require(avd.isNotBlank())
        return processExecutor.execCommand(
            command = buildString {
                append(" -avd $avd")
                if (noWindow) append(" -no-window")
                if (noAudio) append(" -no-audio")
                if (noBootAnim) append(" -no-boot-anim")
            }
        )
    }
}
