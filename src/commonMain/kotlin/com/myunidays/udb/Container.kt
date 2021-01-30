package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient

object Container : Injectable {

    private val adbClient: AdbClient by AdbClientBroker()

    private val udb: Udb = Udb(adb = adbClient)

    override fun adbClient(): AdbClient = adbClient

    override fun udb(): Udb = udb
}
