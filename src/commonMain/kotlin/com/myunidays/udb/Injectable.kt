package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient

interface Injectable {
    fun udb(): Udb
    fun adbClient(): AdbClient
    fun emulatorClient(): EmulatorClient
}
