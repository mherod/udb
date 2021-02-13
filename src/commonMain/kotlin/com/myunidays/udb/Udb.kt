package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient
import com.myunidays.udb.adb.disconnect
import com.myunidays.udb.adb.model.AdbDevice
import com.myunidays.udb.networking.ArpClient
import com.myunidays.udb.util.extractGroup
import com.myunidays.udb.util.launchBlocking
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Udb(
    private val adb: AdbClient,
    private val emulator: EmulatorClient,
    private val arp: ArpClient,
) {
    fun listDevices(): Flow<AdbDevice> = adb.devices()

    @OptIn(ExperimentalTime::class)
    @FlowPreview
    @ExperimentalCoroutinesApi
    fun discoverAndConnect(): Flow<AdbDevice> = flow {
        discoverHosts().flatMapMerge(concurrency = 16) { host ->
            flow {
                supervisorScope {
                    adb.connect(host)
                        .launchIn(this)
                }
                delay(5_000)
                emit(0)
            }
        }.debounce(1.seconds).flatMapConcat {
            killLongProcesses()
        }.toList()
        emitAll(adb.devices())
    }

    private fun discoverHosts() = merge(
        bonjourHosts(),
        networkHosts()
    )

    private fun killLongProcesses(): Flow<String> {
        return exec("pgrep adb")
            .drop(1) // first is likely to be the server!!
            .flatMapMerge { exec("kill $it") }
    }

    private fun networkHosts() = arp.list().map { it.address }

    private fun bonjourHosts(): Flow<String> {
        return bash("dns-sd -B _adb._tcp. & sleep 1 && pgrep dns-sd | xargs kill -13")
            .mapNotNull {
                "adb-\\S+".toRegex().find(it)?.value
            }.flatMapConcat { instanceName ->
                bash("dns-sd -L \"$instanceName\" _adb._tcp. & sleep 5 && pgrep dns-sd | xargs kill -13")
            }.mapNotNull {
                "can be reached at (\\S+)".toRegex().find(it)?.groupValues?.lastOrNull()
            }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun goWireless(): Flow<AdbDevice> {
        return adb.devices()
            .flatMapConcat { adbDevice ->
                if (adbDevice.status == AdbDevice.Status.Offline) {
                    adb.disconnect(adbDevice).singleOrNull()
                }
                flowOf(adbDevice)
            }
            .filterIsInstance<AdbDevice>()
            .filterNot { adbDevice ->
                adbDevice.status == AdbDevice.Status.Offline
            }
            .filterNot { adbDevice ->
                // TODO this better
                adbDevice.name.endsWith(":5555")
            }
            .flatMapConcat { adbDevice ->
                val singleDeviceClient = adb.singleDeviceClient(target = adbDevice)
                singleDeviceClient.execCommand("shell ip addr show wlan0")
                    .mapNotNull { ipRaw ->
                        "inet (\\S+)/.+ wlan0".toRegex()
                            .extractGroup(ipRaw)
                    }
                    .flatMapConcat { host ->
                        singleDeviceClient.execCommand("tcpip 5555").singleOrNull()
                        adb.connect(host)
                    }
            }.flatMapLatest {
                adb.devices()
            }
    }

    fun setupEmulator(
        wait: Boolean = false,
        quiet: Boolean = false,
    ) {
        val beforeDevices: List<AdbDevice> = runBlocking {
            adb.devices().toList()
        }

        emulator.listAvds()
            .flatMapConcat { avdName ->
                emulator.launch(
                    avd = avdName,
                    quiet = quiet,
                )
            }.launchIn(GlobalScope)

        if (wait) runBlocking {
            println("Waiting for device connection")
            while (adb.devices().count { it !in beforeDevices } == 0) {
                delay(800)
            }
        }
    }

    fun tearDownEmulator(wait: Boolean = false) {

        adb.emu(kill = true).launchBlocking()

        if (wait) runBlocking {
            println("Waiting for device disconnection")
            waitForDeviceDisconnection()
        }
    }

    private suspend fun waitForDeviceDisconnection() {
        while (adb.devices().count() > 0) delay(800)
    }
}

//                bash(
//                    command = list.joinToString(separator = " ; ", postfix = " ; wait") {
//                        "nohup adb connect $it & sleep 5 && pgrep adb | xargs kill -13"
//                    }
//                ).toList()
