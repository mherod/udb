package com.myunidays.udb.adb

import com.myunidays.udb.AdbClientBroker
import com.myunidays.udb.Container
import com.myunidays.udb.Udb
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.rules.Timeout
import kotlin.test.Test

class UdbUiTest {

    @get:Rule
    val timeout: TestRule = Timeout.seconds(90)

    private val udb: Udb by Container

    private val adb: AdbClient by AdbClientBroker()

    @Before
    fun setUp() {
        udb.setupEmulator(wait = true, quiet = false)
    }

    @After
    fun tearDown() {
        udb.tearDownEmulator(wait = true)
    }

    @Test
    fun exampleUiTest() {
        runBlocking {
            adb.open("http://www.bbc.co.uk")
            adb.uiautomator()
                .uiNodes()
                .filter { "BBC" in it.toString() }
                .onEach { uiNode -> println(uiNode) }
                .launchIn(this)
        }
    }
}
