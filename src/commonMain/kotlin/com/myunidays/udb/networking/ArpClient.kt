package com.myunidays.udb.networking

import com.myunidays.udb.networking.model.ArpEntry
import kotlinx.coroutines.flow.Flow

interface ArpClient {
    fun list(): Flow<ArpEntry>
}
