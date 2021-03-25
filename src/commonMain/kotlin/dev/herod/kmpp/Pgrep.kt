package dev.herod.kmpp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

fun pgrep(processName: String): Flow<Int> {
    return exec("pgrep $processName").mapNotNull { it.toIntOrNull() }
}
