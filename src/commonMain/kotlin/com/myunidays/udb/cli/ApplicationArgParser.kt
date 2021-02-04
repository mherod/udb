package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.runBlocking
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalCli::class)
class ApplicationArgParser(
    rawArgs: Array<String> = emptyArray(),
    adb: AdbClient = Container.adbClient(),
) : ArgParser(programName = "udb") {

    init {
        when (rawArgs.firstOrNull()) {
            null -> {
                println("Usage: udb --help")
            }
            "adb" -> {
                adb.execArgsPrint(rawArgs)
            }
            else -> {
                subcommands(
                    VersionSubcommand(),
                    DevicesSubcommand(),
                    EmulatorSubcommand(),
                    LogcatSubcommand(),
                    UiSubcommand(),
                )
            }
        }
    }
}

fun AdbClient.execArgsPrint(rawArgs: Array<String>) = runBlocking {
    val command = rawArgs
        .joinToString(" ")
        .substringAfter("adb")
        .trim()
    execCommand(command = command)
        .collect { println(it) }
}
