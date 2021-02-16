package com.myunidays.udb.adb.model

import com.myunidays.udb.util.guessForInput
import org.junit.Test
import kotlin.test.assertEquals

class AdbDeviceTest {

    @Test
    fun getStatus1() {
        assertEquals(
            expected = AdbDevice.Status.Offline,
            actual = guessForInput("error: device offline")
        )
    }

    @Test
    fun getStatus2() {
        assertEquals(
            expected = AdbDevice.Status.Device,
            actual = guessForInput("device")
        )
    }
}
