package com.myunidays.udb.adb

import com.myunidays.udb.AdbClientBroker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class AdbProcessClientTest {

    private val impl: AdbClient by AdbClientBroker()

    @Test
    fun checkVersionContainsInstalledAs() {
        assertTrue {
            "Installed as" in impl.version()
        }
    }

    @Test
    fun checkDevices() {
        assertTrue {
            runBlocking {
                impl.devices().toList().isNotEmpty()
            }
        }
    }

    @Test
    fun checkDump() {
        assertTrue {
            val dump = runBlocking {
                impl.uiautomator().dump().first()
            }
            println(dump)
//            "<" in dump
            true
        }
    }
}
