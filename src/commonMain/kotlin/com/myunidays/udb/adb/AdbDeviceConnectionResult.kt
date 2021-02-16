package com.myunidays.udb.adb

import com.myunidays.udb.adb.model.AdbDevice
import kotlinx.coroutines.flow.Flow

class AdbDeviceConnectionResult(
    val requestHost: String,
    val devices: Flow<AdbDevice>,
    val rawOutput: Flow<String>
)
