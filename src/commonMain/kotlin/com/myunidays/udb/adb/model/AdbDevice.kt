@file:Suppress("unused")

package com.myunidays.udb.adb.model

data class AdbDevice(
    val name: String,
    val status: Status,
    val connectionType: ConnectionType,
) {
    enum class Status {
        Device, Offline, Unauthorized
    }
    enum class ConnectionType {
        USB, Network, Emulator
    }

    companion object {
        fun guessDeviceType(name: String): ConnectionType = when {
            ".+:\\d+".toRegex().matches(name) -> {
                ConnectionType.Network
            }
            "emulator-\\d+".toRegex().matches(name) -> {
                ConnectionType.Emulator
            }
            else -> ConnectionType.USB
        }
    }
}
