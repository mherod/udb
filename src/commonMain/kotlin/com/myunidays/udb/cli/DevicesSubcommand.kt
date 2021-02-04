package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.Udb
import com.myunidays.udb.runBlocking
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.flow.collect

@ExperimentalCli
class DevicesSubcommand(
    private val udb: Udb = Container.udb(),
) : Subcommand(
    name = "devices",
    actionDescription = "Output connected devices and emulators"
) {
    override fun execute() = runBlocking {

        udb.listDevices().collect {
            println(it)
        }
    }
}
