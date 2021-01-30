package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.stream
import com.myunidays.udb.util.changes
import com.myunidays.udb.runBlocking
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.flow.collect

@ExperimentalCli
class UiSubcommand(
    private val adb: AdbClient = Container.adbClient(),
) : Subcommand(
    name = "ui",
    actionDescription = "Extract and automate UI"
) {

    override fun execute() = runBlocking {
        adb.uiautomator()
            .stream()
            .changes()
            .collect { (from, to) ->
                val commonPrefix = from.commonPrefixWith(to)
                val commonSuffix = from.commonSuffixWith(to)
                val justChange = to.substringAfter(commonPrefix).substringBeforeLast(commonSuffix)
                println(justChange)
            }
    }
}
