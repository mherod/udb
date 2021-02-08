package com.myunidays.udb

import com.myunidays.udb.adb.getValue
import kotlinx.coroutines.flow.collect
import org.junit.Test

class UdbTest {

    private val impl: Udb by Container

    @Test
    fun discoverAndConnect() {
        runBlocking {
            impl.discoverAndConnect().collect {
                println(it)
            }
        }
    }
}
