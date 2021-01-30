package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.AdbProcessClient
import kotlin.reflect.KProperty

class AdbClientBroker {
    private val path: String by lazy {
        findAndroidTool(tool = "adb")
    }

    private val adbClient: AdbClient by lazy {
        AdbProcessClient(adb = path)
    }

    operator fun getValue(container: Container, property: KProperty<*>): AdbClient = adbClient
    operator fun getValue(nothing: Nothing?, property: KProperty<*>): AdbClient = adbClient
    operator fun getValue(any: Any, property: KProperty<*>): AdbClient = adbClient
}
