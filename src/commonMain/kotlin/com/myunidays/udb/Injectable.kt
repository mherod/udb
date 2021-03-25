package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient
import dev.herod.kmpp.networking.ArpClient


interface Injectable {
    fun udb(): Udb
    fun adbClient(): AdbClient
    fun emulatorClient(): EmulatorClient
    fun arpClient(): ArpClient
}
