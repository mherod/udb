package com.myunidays.udb.util

import kotlin.test.Test
import kotlin.test.assertEquals

class SplitOnSpacingTest {
    @Test
    fun checkSimple() {
        assertEquals(
            expected = listOf("hello", "world"),
            actual = "hello world".splitOnSpacing()
        )
    }

    @Test
    fun checkComplex() {
        assertEquals(
            expected = listOf("hello", "world"),
            actual = "  hello    world\n ".splitOnSpacing()
        )
    }
}
