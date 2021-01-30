package com.myunidays.udb

import kotlinx.coroutines.flow.Flow

interface ProcessExecutor {
    fun execCommand(command: String): Flow<String>
}
