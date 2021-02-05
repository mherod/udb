package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.disconnect
import com.myunidays.udb.adb.model.AdbDevice
import com.myunidays.udb.util.extractGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class Udb(
    private val adb: AdbClient,
) {
    fun version(): String = "udb 0.0.1, using adb ${adb.version()}"

    fun listDevices(): Flow<AdbDevice> = adb.devices()

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun discoverAndConnect(): Flow<AdbDevice> = runBlocking {

        bash("dns-sd -B _adb._tcp. & pgrep dns-sd | xargs kill -13")
            .mapNotNull {
                "adb-\\S+".toRegex().find(it)?.value
            }.flatMapConcat { instanceName ->
                bash("dns-sd -L \"$instanceName\" _adb._tcp. & sleep 5 && pgrep dns-sd | xargs kill -13")
            }.mapNotNull {
                "can be reached at (\\S+)".toRegex().find(it)?.groupValues?.lastOrNull()
            }.flatMapConcat { host: String ->
                adb.connect(host)
            }.flowOn(Dispatchers.Default)
            .launchIn(this)

        adb.devices()
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
