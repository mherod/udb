package com.myunidays.udb.adb

import com.myunidays.udb.AdbClientBroker
import com.myunidays.udb.Container
import com.myunidays.udb.Udb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.rules.Timeout
import kotlin.test.Test
import kotlin.test.assertTrue

class AdbProcessClientTest {

    @get:Rule
    val timeout: TestRule = Timeout.seconds(30)

    private val udb: Udb by Container

    private val impl: AdbClient by AdbClientBroker()

    @Before
    fun setUp() {
        udb.setupEmulator(wait = true, quiet = true)
    }

    @After
    fun tearDown() {
        udb.tearDownEmulator(wait = true)
    }

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
                impl.uiautomator()
                    .dump()
                    .retryWhen { _, _ -> true }
                    .first()
            }
            println(dump)
            true
        }
    }

    @Test
    fun checkUiNodes() {
        assertTrue {
            val uiNodes = runBlocking {
                impl.uiautomator()
                    .uiNodes()
                    .toList()
            }
            println(uiNodes)
            uiNodes.isNotEmpty()
        }
    }
}
