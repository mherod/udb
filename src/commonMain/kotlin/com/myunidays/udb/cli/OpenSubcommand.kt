package com.myunidays.udb.cli

import com.myunidays.udb.Container
import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.runBlocking
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.flow.collect

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

    override fun execute() = runBlocking {
        when {
            path.matches("http.+".toRegex()) -> {
                adb.execCommand(buildString {
                    append("shell")
                    append(" am")
                    append(" start")
                    append(" -a android.intent.action.VIEW")
                    append(" -d $path")
                }).collect { s ->
                    println(s)
                }
            }
            else -> {
//                adb.execCommand("adb shell am start -n \"com.myunidays.dev/com.myunidays.san.onboarding.OnboardingPartnerSelectionActivity\" -a android.intent.action.MAIN").collect { s ->
//                    println(s)
//                }
            }
        }
    }
}
