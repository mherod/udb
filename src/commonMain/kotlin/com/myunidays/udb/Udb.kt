package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.model.AdbDevice
import kotlinx.coroutines.flow.Flow

class Udb(
    private val adb: AdbClient,
) {
    fun version(): String = "udb 0.0.1, using adb ${adb.version()}"

    fun listDevices(): Flow<AdbDevice> = adb.devices()
}
