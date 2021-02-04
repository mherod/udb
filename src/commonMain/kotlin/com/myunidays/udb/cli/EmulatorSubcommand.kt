package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient
import com.myunidays.udb.runBlocking
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn

@ExperimentalCli
class EmulatorSubcommand(
    private val emulatorClient: EmulatorClient = Container.emulatorClient(),
    private val adbClient: AdbClient = Container.adbClient(),
) : Subcommand(
    name = "emulator",
    actionDescription = "Operate on emulators"
) {
    private val start: Boolean by option(
        type = ArgType.Boolean,
        fullName = "start",
        shortName = "s",
        description = "Start an emulator",
    ).default(false)

    private val silent: Boolean by option(
        type = ArgType.Boolean,
        fullName = "silent",
        shortName = "q",
        description = "New emulators are started hidden, not showing the window or sound",
    ).default(false)

    private val stop: Boolean by option(
        type = ArgType.Boolean,
        fullName = "stop",
        shortName = "f",
        description = "Request currently opened emulator to close",
    ).default(false)

    private val list: Boolean by option(
        type = ArgType.Boolean,
        fullName = "list",
        shortName = "l",
        description = "List avds to launch",
    ).default(false)

    @FlowPreview
    override fun execute() = runBlocking {

        if (list) {
            emulatorClient.listAvds().collect {
                println(it)
            }
        }

        if (stop) {
            adbClient.emu(kill = true).collect { s ->
                println(s)
            }
        }

        if (start) {
            emulatorClient.listAvds()
                .flatMapConcat { avdName ->
                    emulatorClient.launch(
                        avd = avdName,
                        quiet = silent,
                    )
                }.launchIn(this)
        }
    }
}
