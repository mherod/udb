package com.myunidays.udb.adb

import com.myunidays.udb.adb.model.AdbDevice
import com.myunidays.udb.adb.model.AdbDevice.Companion.guessDeviceType
import com.myunidays.udb.adb.model.AdbDevice.ConnectionType
import com.myunidays.udb.adb.model.AdbDevice.Status
import com.myunidays.udb.adb.model.AdbLogcatLine
import com.myunidays.udb.exec
import com.myunidays.udb.runBlocking
import com.myunidays.udb.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
data class AdbProcessClient(
    private val adb: String,
    private val workingState: AdbWorkingState = AdbWorkingState(),
) : AdbClient {

    private val serverProcessId: MutableStateFlow<Int> = MutableStateFlow(0)

    init {
        startServer()
    }

    override fun startServer(restart: Boolean) = runBlocking {
        if (restart) {
            exec("adb kill-server")
                .launchIn(this)
            adbDaemonPid()
                .map { pid ->
                    exec("kill $pid")
                }
                .launchIn(this)
        }
        exec("adb start-server").launchIn(this)

        adbDaemonPid().collect { pid ->
            serverProcessId.value = pid
        }
    }

    private fun adbDaemonPid(): Flow<Int> = pgrep("adb").take(1)

    override fun version(): String = runBlocking {
        exec("$adb --version")
            .map { "\\S*\\d+\\S*".toRegex().find(it)?.value ?: it }
            .map { it.trim() }
            .filterNot { it.isBlank() }
            .toList()
            .joinToString()
    }

    override fun devices(): Flow<AdbDevice> =
        // adb devices -l
        exec("$adb devices")
            .filter { it.isNotBlank() }
            .filterNot { it.startsWith('*') }
            .mapNotNull { s ->
                runOrNull {
                    s.splitOnSpacing().takeIf { it.size == 2 }?.let { parsedDeviceLine ->
                        val name = parsedDeviceLine.first()
                        AdbDevice(
                            name = name,
                            status = guessForInput(parsedDeviceLine[1]),
                            connectionType = guessDeviceType(name)
                        )
                    }
                }
            }.flatMapConcat { adbDevice ->
                refreshState(adbDevice)
            }

    override fun singleDeviceClient(target: AdbDevice): AdbClient = copy(
        workingState = AdbWorkingState(targetDevices = flowOf(target))
    )

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
                                priority = guessForInput(parts[4]),
                                tag = tag,
                                message = s.substringAfterLast(tag).trim { it.isWhitespace() || it == ':' }
                            )
                        }
                }
            }

    override fun shell(): AdbShellClient = AdbShellSingleClient()

    override fun uiautomator(): UiAutomatorClient = UiAutomatorProcessClient(adbClient = this)

    override fun connect(host: String): Flow<AdbDeviceConnectionResult> = flow {
        emit(
            value = AdbDeviceConnectionResult(
                rawOutput = exec("$adb connect $host").toList().asFlow(),
                requestHost = host,
                devices = devices().filter { it.name == host }
            )
        )
    }

    override fun disconnect(name: String): Flow<String> = flow {
        val exec = exec("$adb disconnect $name")
        emitAll(flow = exec)
    }

    override fun emu(kill: Boolean): Flow<String> = exec(
        command = buildString {
            append(adb)
            append(" emu")
            if (kill) {
                append(" kill")
            }
        }
    )

    override fun input(): AdbInputClient = AdbInputProcessClient(adbClient = this)

    override fun listPackages(): Flow<String> {
        return devices().flatMapConcat { device ->
            val singleDeviceClient = singleDeviceClient(device)
            singleDeviceClient.execCommand("shell pm list packages")
                .mapNotNull {
                    "package:(.+)".toRegex().extractGroup(it)
                }
        }.sorted()
    }

    override fun listActivities(): Flow<String> {
        return devices().flatMapConcat { device ->
            val singleDeviceClient = singleDeviceClient(device)
            singleDeviceClient.execCommand("shell pm list packages")
                .mapNotNull { "package:(.+)".toRegex().extractGroup(it) }
                .sorted()
                .flatMapConcat { packageName ->
                    singleDeviceClient.listActivities(packageName)
                }
        }
    }

    override fun execCommand(command: String): Flow<String> = flow {
        val all = clientTargetDevices()
            .filter { it.status == Status.Device }
            .flatMapMerge { device ->
                exec("$adb -s ${device.name} $command")
            }
        emitAll(all)
    }

    private fun clientTargetDevices(): Flow<AdbDevice> {
        return workingState.targetDevices
            .flatMapConcat { refreshState(it) }
            .flatMapConcat { it.waitForDevice() }
            .onEmpty { emitAll(flow = devices()) }
    }

    private fun AdbDevice.waitForDevice(): Flow<AdbDevice> {
        val adbDevice = this
        return exec("$adb -s $name wait-for-device")
            .map { adbDevice }
            .onEmpty { emit(adbDevice) }
            .flatMapConcat { refreshState(it) }
            .distinctUntilChanged()
    }

    override fun refreshState(adbDevice: AdbDevice): Flow<AdbDevice> {
        return exec("$adb -s ${adbDevice.name} get-state")
            .map { adbDevice.copy(status = guessForInput(it)) }
            .flatMapConcat { adbDevice ->
                when (adbDevice.status) {
                    Status.Offline -> when (adbDevice.connectionType) {
                        ConnectionType.Network -> {
                            disconnect(adbDevice).launchBlocking()
                            connect(host = adbDevice.name).flatMapConcat { it.devices }
                        }
                        ConnectionType.Emulator -> {
                            emu(kill = true).launchBlocking()
                            emptyFlow()
                        }
                        else -> flowOf(adbDevice)
                    }
                    else -> flowOf(adbDevice)
                }
            }
    }

    override fun service(serviceName: String, enable: Boolean): Flow<String> {
        TODO("Not yet implemented")
    }

}

fun AdbClient.listActivities(
    packageName: String,
): Flow<String> {
    return execCommand("shell pm dump $packageName")
        .filter { "Activity" in it }
        .mapNotNull { s ->
            val escapedPackage = packageName
                .replace(".", "\\.")
            "($escapedPackage/\\S+Activity)"
                .toRegex()
                .extractGroup(s)
        }.sorted()
}
