package dev.herod.kmpp.networking

import dev.herod.kmpp.runBlocking
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.junit.Test
import kotlin.test.assertTrue

class SystemNetworkSetupClientTest {

    private val networkSetupClient = SystemNetworkSetupClient()

    @Test
    fun queryDnsServers() {
        assertTrue {
            runBlocking {
                networkSetupClient.queryDnsServers()
                    .onEach { println(it) }
                    .toList()
                    .isNotEmpty()
            }
        }
    }
}
