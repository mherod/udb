package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient
import com.myunidays.udb.adb.model.AdbDevice
import com.myunidays.udb.util.*
import dev.herod.kmpp.networking.ArpClient
import dev.herod.kmpp.networking.BonjourClient
import dev.herod.kx.flow.timeout
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime

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

        discoverHosts()
            .flatMapMerge { host: String ->
                adb.connect(host)
                    .flowOn(Dispatchers.Default)
                    .timeout(1_000)
            }
            .timeout(5_000)
            .toList()

        emitAll(flow = adb.devices())
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
            .filter { it.connectionType == AdbDevice.ConnectionType.USB }
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
            while (adb.devices().any { it.connectionType == AdbDevice.ConnectionType.Emulator }) {
                delay(800)
            }
        }
    }
}
