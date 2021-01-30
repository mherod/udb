package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.runBlocking
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.flow.collect

@ExperimentalCli
class LogcatSubcommand(
    private val adb: AdbClient = Container.adbClient(),
) : Subcommand(
    name = "logcat",
    actionDescription = "Get device logs"
) {
    override fun execute() = runBlocking {
        adb.logs().collect { logLine ->
            println(logLine)
        }
    }
}
