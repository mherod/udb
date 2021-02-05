package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.listActivities
import com.myunidays.udb.runBlocking
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat

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
        when {
            path.matches("http.+".toRegex()) -> {
                adb.execCommand(
                    command = buildString {
                        append("shell")
                        append(" am")
                        append(" start")
                        append(" -a android.intent.action.VIEW")
                        append(" -d $path")
                    }
                ).collect { s ->
                    println(s)
                }
            }
            else -> {
                adb.listPackages()
                    .filter { it in path }
                    .flatMapConcat { packageName ->
                        val searchParts = path
                            .split("\\W".toRegex())
                            .filterNot { it.isBlank() }
                        adb.listActivities(packageName)
                            .filter { activity ->
                                searchParts.all {
                                    activity.contains(it, ignoreCase = true)
                                }
                            }
                            .flatMapConcat { activityPath ->
                                println(activityPath)
                                adb.execCommand(
                                    command = buildString {
                                        append("shell")
//                                        append(" run-as")
//                                        append(" $packageName")
                                        append(" am")
                                        append(" start")
                                        append(" -n \"$activityPath\"")
                                    }
                                )
                            }
                    }
                    .collect { s ->
                        println(s)
                    }
            }
        }
    }
}
