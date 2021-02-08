package com.myunidays.udb.networking

import com.myunidays.udb.Container
import com.myunidays.udb.adb.getValue
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertTrue

class ArpProcessClientTest {

    private val impl: ArpProcessClient by Container

    @Test
    fun listArp() = assertTrue {
        runBlocking {
            impl.list().onEach { arpEntry ->
                println(arpEntry)
            }.toList().isNotEmpty()
        }
    }
}
