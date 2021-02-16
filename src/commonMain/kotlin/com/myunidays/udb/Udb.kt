package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient
import com.myunidays.udb.adb.model.AdbDevice
import com.myunidays.udb.adb.model.AdbDevice.ConnectionType.Emulator
import com.myunidays.udb.adb.model.AdbDevice.ConnectionType.USB
import com.myunidays.udb.networking.ArpClient
import com.myunidays.udb.networking.BonjourClient
import com.myunidays.udb.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Udb(
    private val adb: AdbClient,
    private val emulator: EmulatorClient,
    private val arp: ArpClient,
    private val bonjour: BonjourClient,
) {

    fun listDevices(): Flow<AdbDevice> = adb.devices()

    @OptIn(ExperimentalTime::class)
    @FlowPreview
    @ExperimentalCoroutinesApi
    fun discoverAndConnect(): Flow<AdbDevice> = flow {
        discoverHosts().flatMapMerge { host ->
            flow {
                supervisorScope {
                    adb.connect(host).launchIn(this)
                }
                delay(5_000)
                emit(0)
            }
        }.debounce(1.seconds).flatMapConcat {
            killLongProcesses()
        }.toList()
        emitAll(adb.devices())
    }

    private fun discoverHosts(): Flow<String> = merge(
        networkHosts(),
        bonjourHosts()
    )

    private fun killLongProcesses(): Flow<String> {
        return pgrep("adb")
            .drop(1) // first is likely to be the server!!
            .flatMapMerge { kill(it) }
    }

    private fun networkHosts(): Flow<String> = arp.list().map { it.address }

    private fun bonjourHosts(): Flow<String> = bonjour.queryServiceHosts("_adb._tcp.")

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun goWireless(): Flow<AdbDevice> {
        return adb.devices()
            .flatMapConcat { adb.refreshState(it) }
            .filterIsInstance<AdbDevice>()
            .filterNot { it.status == AdbDevice.Status.Offline }
            .filter { it.connectionType == USB }
            .flatMapConcat { adbDevice ->
                val singleDeviceClient = adb.singleDeviceClient(target = adbDevice)
                singleDeviceClient.execCommand("shell ip addr show wlan0")
                    .mapNotNull { ipRaw ->
                        "inet (\\S+)/.+ wlan0".toRegex().extractGroup(ipRaw)
                    }
                    .onEach {
                        singleDeviceClient.execCommand("tcpip 5555").launchBlocking()
                    }
                    .flatMapConcat { host ->
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
            println("Waiting for emulator disconnection")
            while (adb.devices().any { it.connectionType == Emulator }) {
                delay(800)
            }
        }
    }
}
