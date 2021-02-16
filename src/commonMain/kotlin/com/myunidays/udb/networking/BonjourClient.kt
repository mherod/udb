package com.myunidays.udb.networking

import kotlinx.coroutines.flow.Flow

interface BonjourClient {
    fun queryServiceHosts(type: String): Flow<String>
}
