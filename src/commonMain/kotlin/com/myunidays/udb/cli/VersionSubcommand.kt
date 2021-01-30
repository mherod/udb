package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.Udb
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@ExperimentalCli
class VersionSubcommand(
    private val udb: Udb = Container.udb(),
) : Subcommand(
    name = "version",
    actionDescription = "Print tool version"
) {
    override fun execute() = println(udb.version())
}
