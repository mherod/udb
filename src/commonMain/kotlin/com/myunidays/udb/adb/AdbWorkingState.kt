package com.myunidays.udb.adb

import com.myunidays.udb.adb.model.AdbDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class AdbWorkingState(
    val targetDevices: Flow<AdbDevice> = emptyFlow()
)
