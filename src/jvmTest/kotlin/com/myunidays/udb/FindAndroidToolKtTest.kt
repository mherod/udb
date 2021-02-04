package com.myunidays.udb

import org.junit.Test
import kotlin.test.assertNotNull

class FindAndroidToolKtTest {

    @Test
    fun findAndroidTool_adb(): Unit = runBlocking {
        assertNotNull(
            actual = findAndroidTool("adb"),
        )
    }

    @Test
    fun findAndroidTool_emulator(): Unit = runBlocking {
        assertNotNull(
            actual = findAndroidTool("emulator"),
        )
    }
}
