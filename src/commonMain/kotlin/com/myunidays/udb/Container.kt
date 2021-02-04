package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient

object Container : Injectable {

    private val adbClient: AdbClient by AdbClientBroker()

    private val emulator: EmulatorClient by EmulatorClientBroker()

    private val udb: Udb = Udb(adb = adbClient)

    override fun udb(): Udb = udb

    override fun adbClient(): AdbClient = adbClient

    override fun emulatorClient(): EmulatorClient = emulator
}
