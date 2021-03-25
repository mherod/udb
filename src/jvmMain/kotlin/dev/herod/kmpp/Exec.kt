package dev.herod.kmpp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

actual fun exec(command: String): Flow<String> {
    println(command)
    return Runtime.getRuntime()
        .exec(command)
        .mergedInputStreamFlow()
        .onEach { s ->
            println("<< $s")
        }
}
