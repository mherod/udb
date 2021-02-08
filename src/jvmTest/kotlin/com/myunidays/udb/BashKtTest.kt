package com.myunidays.udb

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import org.junit.Test
import kotlin.test.assertTrue

class BashKtTest {

    @Test
    fun bashPathTest() = assertTrue {
        runBlocking {
            bash("echo \$PATH").single().isNotBlank()
        }
    }

    @Test
    fun bashEnvTest() = assertTrue {
        runBlocking {
            bash("env").count() > 0
        }
    }
}
