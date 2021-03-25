package dev.herod.kmpp.networking

import dev.herod.kmpp.bash
import dev.herod.kx.extractGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.mapNotNull

class BonjourProcessClient : BonjourClient {
    override fun queryServiceHosts(type: String): Flow<String> {
        // TODO parameterize this
        return bash("dns-sd -B $type & sleep 1 && pgrep dns-sd | xargs kill -13")
            .mapNotNull {
                "adb-\\S+".toRegex().find(it)?.value
            }.flatMapConcat { instanceName ->
                bash("dns-sd -L \"$instanceName\" $type & sleep 5 && pgrep dns-sd | xargs kill -13")
            }.mapNotNull {
                "can be reached at (\\S+)".toRegex().extractGroup(it)
            }
    }
}
