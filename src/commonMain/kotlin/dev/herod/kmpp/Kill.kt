package dev.herod.kmpp

import kotlinx.coroutines.flow.Flow

fun kill(pid: Int): Flow<String> = exec("kill $pid")
