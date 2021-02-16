package com.myunidays.udb.networking

import com.myunidays.udb.exec
import com.myunidays.udb.util.distinct
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
