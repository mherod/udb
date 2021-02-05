package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.Udb
import com.myunidays.udb.runBlocking
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.coroutines.flow.collect

@ExperimentalCli
class DevicesSubcommand(
    private val udb: Udb = Container.udb(),
) : Subcommand(
    name = "devices",
    actionDescription = "Output connected devices and emulators"
) {

    private val wifiScan: Boolean by option(
        type = ArgType.Boolean,
        fullName = "network-scan",
        shortName = "n",
    ).default(false)

    private val fly: Boolean by option(
        type = ArgType.Boolean,
        fullName = "fly",
        shortName = "f",
    ).default(false)

    override fun execute() = runBlocking {

        when {
            fly -> {
                udb.goWireless().collect {
                    println(it)
                }
            }
            wifiScan -> {
                udb.discoverAndConnect().collect {
                    println(it)
                }
            }
            else -> {
                udb.listDevices().collect {
                    println(it)
                }
            }
        }
    }
}
