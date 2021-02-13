package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.open
import com.myunidays.udb.runBlocking
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCli
class OpenSubcommand(
    private val adb: AdbClient = Container.adbClient(),
) : Subcommand(
    name = "open",
    actionDescription = "Open something on a connected device"
) {
    private val path: String by argument(
        type = ArgType.String,
        fullName = "path"
    )

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun execute() = runBlocking {
        adb.open(path)
    }
}
