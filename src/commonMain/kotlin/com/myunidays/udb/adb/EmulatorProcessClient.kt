package com.myunidays.udb.adb

import com.myunidays.udb.ProcessExecutor
import com.myunidays.udb.networking.NetworkSetupClient
import kotlinx.coroutines.flow.*

class EmulatorProcessClient(
    private val processExecutor: ProcessExecutor,
    private val networkSetupClient: NetworkSetupClient
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
    ): Flow<String> = flow {
        require(avd.isNotBlank())
        val dnsString = networkSetupClient.queryDnsServers()
            .toList()
            .joinToString(",")
        emitAll(
            flow = processExecutor.execCommand(
                command = buildString {
                    append(" -avd $avd")
                    if (noWindow) append(" -no-window")
                    if (noAudio) append(" -no-audio")
                    if (noBootAnim) append(" -no-boot-anim")
                    append(" -dns-server $dnsString")
                }
            )
        )
    }
}
