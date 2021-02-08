package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.disconnect
import com.myunidays.udb.adb.model.AdbDevice
import com.myunidays.udb.networking.ArpClient
import com.myunidays.udb.util.extractGroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Udb(
    private val adb: AdbClient,
    private val arp: ArpClient,
) {
    fun listDevices(): Flow<AdbDevice> = adb.devices()

    @OptIn(ExperimentalTime::class)
    @FlowPreview
    @ExperimentalCoroutinesApi
    fun discoverAndConnect(): Flow<AdbDevice> = flow {
        merge(
            bonjourHosts(),
            networkHosts()
        ).flatMapMerge { host ->
            flow {
                adb.connect(host).launchIn(GlobalScope)
                delay(5_000)
                emit(0)
            }
        }.debounce(1.seconds).flatMapConcat {
            killLongProcesses()
        }.toList()
        emitAll(adb.devices())
    }

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
}

//                bash(
//                    command = list.joinToString(separator = " ; ", postfix = " ; wait") {
//                        "nohup adb connect $it & sleep 5 && pgrep adb | xargs kill -13"
//                    }
//                ).toList()
