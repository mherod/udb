package com.myunidays.udb.adb

import com.myunidays.udb.adb.model.AdbDevice
import com.myunidays.udb.adb.model.AdbLogcatLine
import com.myunidays.udb.exec
import com.myunidays.udb.runBlocking
import com.myunidays.udb.util.matchByName
import com.myunidays.udb.util.runOrNull
import com.myunidays.udb.util.splitOnSpacing
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class AdbProcessClient(private val adb: String) : AdbClient {

    override fun version(): String = runBlocking {
        exec("$adb --version")
            .map { "\\S*\\d+\\S*".toRegex().find(it)?.value ?: it }
            .map { it.trim() }
            .filterNot { it.isBlank() }
            .toList()
            .joinToString()
    }

    override fun devices(): Flow<AdbDevice> =
        exec("$adb devices")
            .filter { it.isNotBlank() }
            .filterNot { it.startsWith('*') }
            .mapNotNull { s ->
                runOrNull {
                    s.splitOnSpacing().takeIf { it.size == 2 }?.let { parsedDeviceLine ->
                        println(parsedDeviceLine)
                        AdbDevice(
                            name = parsedDeviceLine.first(),
                            status = matchByName(parsedDeviceLine[1])
                        )
                    }
                }
            }

    @FlowPreview
    override fun logs(): Flow<AdbLogcatLine> =
        execCommand("logcat")
            .filterNot { it.startsWith('*') }
            .mapNotNull { s ->
                runOrNull {
                    s.splitOnSpacing()
                        .takeIf { it.size >= 6 }
                        ?.let { parts ->
                            val tag = parts[5]
                            AdbLogcatLine(
                                date = parts[0],
                                time = parts[1],
                                pid = parts[2].toInt(),
                                tid = parts[3].toInt(),
                                priority = matchByName(parts[4]),
                                tag = tag,
                                message = s.substringAfterLast(tag).trim { it.isWhitespace() || it == ':' }
                            )
                        }
                }
            }

    override fun shell(): AdbShellClient = AdbShellSingleClient()

    override fun uiautomator(): UiAutomatorClient = UiAutomatorProcessClient(adbClient = this)

    @FlowPreview
    override fun execCommand(command: String): Flow<String> = flow {
        val all = devices()
            .filterNot { it.status == AdbDevice.Status.Offline }
            .onEmpty {
                error("no connected devices")
            }
            .flatMapMerge { device ->
                exec("$adb -s ${device.name} $command")
            }
        emitAll(all)
    }
}
