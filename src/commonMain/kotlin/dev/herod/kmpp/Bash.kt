package dev.herod.kmpp

import kotlinx.coroutines.flow.Flow

expect fun bash(command: String): Flow<String>
