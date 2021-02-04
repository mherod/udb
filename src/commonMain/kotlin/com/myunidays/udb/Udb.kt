package com.myunidays.udb

import com.myunidays.udb.adb.AdbClient
import com.myunidays.udb.adb.model.AdbDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class Udb(
    private val adb: AdbClient,
) {
    fun version(): String = "udb 0.0.1, using adb ${adb.version()}"

    fun listDevices(): Flow<AdbDevice> = adb.devices()

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun discoverAndConnect(): Flow<AdbDevice> = runBlocking {

        bash("dns-sd -B _adb._tcp. & pgrep dns-sd | xargs kill -13")
            .mapNotNull {
                "adb-\\S+".toRegex().find(it)?.value
            }.flatMapConcat { instanceName ->
                bash("dns-sd -L \"$instanceName\" _adb._tcp. & sleep 5 && pgrep dns-sd | xargs kill -13")
            }.mapNotNull {
                "can be reached at (\\S+)".toRegex().find(it)?.groupValues?.lastOrNull()
            }.flatMapConcat { host: String ->
                adb.connect(host)
            }.flowOn(Dispatchers.Default)
            .launchIn(this)

        adb.devices()
    }
}
