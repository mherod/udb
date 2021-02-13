package com.myunidays.udb.util

import com.myunidays.udb.cli.EmulatorSubcommand
import com.myunidays.udb.runBlocking
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun String.splitOnSpacing(): List<String> = split("\\s+".toRegex()).filterNot { it.isBlank() }

inline fun <reified A : Enum<A>> matchByName(name: String): A {
    return enumValues<A>().single { it.name.equals(name, ignoreCase = true) }
}

fun String?.isNotNullOrBlank(): Boolean = isNullOrBlank().not()

fun Regex.extractGroup(input: String): String? = find(input)?.groupValues?.lastOrNull()

infix fun String.attributeString(name: String): String =
    "$name=\"([^\"]*)\"".toRegex().find(this)?.groupValues?.lastOrNull().orEmpty()

infix fun String.attributeBoolean(name: String): Boolean = attributeString(name).toBoolean()

fun <T> Flow<T>.launchBlocking(): Job = runBlocking { launchIn(this) }

@ExperimentalTime
suspend fun <T> EmulatorSubcommand.maybeTimeout(duration: Duration?, function: () -> T): T {
    return duration?.let { timeoutDuration ->
        withTimeoutOrNull(timeoutDuration) {
            function()
        }
    } ?: run {
        function()
    }
}
