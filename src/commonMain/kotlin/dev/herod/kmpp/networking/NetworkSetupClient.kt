package dev.herod.kmpp.networking

import kotlinx.coroutines.flow.Flow

interface NetworkSetupClient {
    fun queryDnsServers(): Flow<String>
}
