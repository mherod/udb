package com.myunidays.udb

import kotlinx.coroutines.flow.collect
import kotlin.test.Test

class UdbTest {

    private val impl: Udb = Container.udb()

    @Test
    fun discoverAndConnect() {
        runBlocking {
            impl.discoverAndConnect().collect {
                println(it)
            }
        }
    }
}
