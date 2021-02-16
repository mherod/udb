package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.EmulatorClient
import com.myunidays.udb.networking.ArpClient
import com.myunidays.udb.networking.ArpProcessClient
import com.myunidays.udb.networking.BonjourClient
import com.myunidays.udb.networking.BonjourProcessClient

object Container : Injectable {

    private val adbClient: AdbClient by AdbClientBroker()

    private val emulator: EmulatorClient by EmulatorClientBroker()

    private val arpClient: ArpClient by lazy(::ArpProcessClient)

    private val bonjourClient: BonjourClient by lazy(::BonjourProcessClient)

    private val udb: Udb = Udb(
        adb = adbClient,
        emulator = emulator,
        arp = arpClient,
        bonjour = bonjourClient
    )

    override fun udb(): Udb = udb

    override fun adbClient(): AdbClient = adbClient

    override fun emulatorClient(): EmulatorClient = emulator

    override fun arpClient(): ArpClient = arpClient
}
