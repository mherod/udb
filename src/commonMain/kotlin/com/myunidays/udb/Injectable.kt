package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient

interface Injectable {
    fun adbClient(): AdbClient
    fun udb(): Udb
}
