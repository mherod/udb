package dev.herod.kmpp.networking

import dev.herod.kmpp.networking.model.ArpEntry
import kotlinx.coroutines.flow.Flow

interface ArpClient {
    fun list(): Flow<ArpEntry>
}
