package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@ExperimentalCli
class VersionSubcommand(
    private val adb: AdbClient = Container.adbClient(),
) : Subcommand(
    name = "version",
    actionDescription = "Print tool version"
) {
    override fun execute() {
        println("udb 0.0.1, using adb ${adb.version()}")
    }
}
