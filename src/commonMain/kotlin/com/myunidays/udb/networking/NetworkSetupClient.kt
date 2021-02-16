package com.myunidays.udb.networking

import kotlinx.coroutines.flow.Flow

interface NetworkSetupClient {
    fun queryDnsServers(): Flow<String>
}
