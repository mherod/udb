package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.stream
import com.myunidays.udb.adb.tap
import com.myunidays.udb.adb.uiNodes
import com.myunidays.udb.runBlocking
import com.myunidays.udb.util.changes
import com.myunidays.udb.util.isNotNullOrBlank
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapMerge

class UiSubcommand(
    private val adb: AdbClient = Container.adbClient(),
) : Subcommand(
    name = "ui",
    actionDescription = "Extract and automate UI"
) {
    private val watch: Boolean by option(
        type = ArgType.Boolean
    ).default(false)

    private val tap: String? by option(
        type = ArgType.String,
        fullName = "tap",
        shortName = "t"
    )

    private val type: String? by option(
        type = ArgType.String,
        fullName = "type",
        shortName = "e"
    )

    @FlowPreview
    override fun execute() = runBlocking {
        adb.uiautomator().run {
            when {
                watch -> {
                    stream().changes().collect { (from, to) ->
                        val commonPrefix = from.commonPrefixWith(to)
                        val commonSuffix = from.commonSuffixWith(to)
                        val justChange = to.substringAfter(commonPrefix).substringBeforeLast(commonSuffix)
                        println(justChange)
                    }
                }
                tap.isNotNullOrBlank() -> {
                    val adbInputClient = adb.input()
                    uiNodes().filter { node ->
                        node.text == tap
                    }.flatMapMerge { node ->
                        adbInputClient.tap(node)
                    }.collect { s ->
                        println(s)
                    }
                }
                type.isNotNullOrBlank() -> {
                    adb.input().text(text = type.orEmpty()).collect { s ->
                        println(s)
                    }
                }
                else -> {
                    uiNodes().filterNot { uiNode ->
                        uiNode.clazz.isBlank()
                    }.collect { s ->
                        println(s)
                    }
                }
            }
        }
    }
}
