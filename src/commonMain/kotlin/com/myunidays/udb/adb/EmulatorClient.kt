package com.myunidays.udb.adb

import kotlinx.coroutines.flow.Flow

interface EmulatorClient {
    fun listAvds(): Flow<String>

    fun launch(
        avd: String,
        quiet: Boolean = false,
        noWindow: Boolean = quiet,
        noAudio: Boolean = quiet,
        noBootAnim: Boolean = quiet,
    ): Flow<String>
}
