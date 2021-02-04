package com.myunidays.udb.adb.model

data class AdbDevice(
    val name: String,
    val status: Status,
) {
    enum class Status {
        Device, Offline, Unauthorized
    }
}
