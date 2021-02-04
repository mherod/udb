package com.myunidays.udb.adb

import com.myunidays.udb.Container
import com.myunidays.udb.runBlocking
import kotlinx.coroutines.flow.collect
import kotlin.test.Test

class EmulatorProcessClientTest {

    private val emulatorClient: EmulatorClient by Container

    @Test
    fun listAvds() {
        runBlocking {
            emulatorClient.listAvds().collect {
                println(it)
            }
        }
    }
}
