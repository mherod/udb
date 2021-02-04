package com.myunidays.udb.adb

import kotlinx.coroutines.flow.Flow

interface EmulatorClient {
    fun listAvds(): Flow<String>
    fun launch(avd: String): Flow<String>
}
