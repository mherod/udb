package dev.herod.kmpp.networking

import dev.herod.kx.flow.distinct
import dev.herod.kmpp.exec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat

class SystemNetworkSetupClient : NetworkSetupClient {
    override fun queryDnsServers(): Flow<String> =
        exec("networksetup -listallnetworkservices")
            .filter { " " !in it }
            .flatMapConcat { service ->
                exec("networksetup -getdnsservers $service")
                    .filter { "(\\d+\\.?)+".toRegex().matches(it) }
            }
            .distinct()
}
