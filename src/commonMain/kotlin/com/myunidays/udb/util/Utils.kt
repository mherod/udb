package com.myunidays.udb.util

import com.myunidays.udb.cli.EmulatorSubcommand
import com.myunidays.udb.runBlocking
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun String.splitOnSpacing(): List<String> = split("\\s+".toRegex()).filterNot { it.isBlank() }

inline fun <reified A : Enum<A>> guessForInput(name: String): A {
    return name.splitOnSpacing()
        .asReversed()
        .mapNotNull { part ->
            enumValues<A>().singleOrNull { enumValue ->
                enumValue.name.equals(part, ignoreCase = true)
            }
        }.first()
}

fun String?.isNotNullOrBlank(): Boolean = isNullOrBlank().not()

fun Regex.extractGroup(input: String): String? = find(input)?.groupValues?.lastOrNull()

infix fun String.attributeString(name: String): String =
    "$name=\"([^\"]*)\"".toRegex().find(this)?.groupValues?.lastOrNull().orEmpty()

infix fun String.attributeBoolean(name: String): Boolean = attributeString(name).toBoolean()

fun <T> Flow<T>.launchBlocking(): Job = runBlocking { launchIn(this) }

@ExperimentalTime
suspend fun <T> EmulatorSubcommand.maybeTimeout(duration: Duration?, function: suspend () -> T): T {
    return duration?.let { timeoutDuration ->
        withTimeoutOrNull(timeoutDuration) {
            function()
        }
    } ?: run {
        function()
    }
}

suspend fun <T> Flow<T>.any(function: (T) -> Boolean): Boolean = count { function(it) } > 0

inline fun <reified T : Comparable<T>> Flow<T>.sorted(): Flow<T> = flow {
    toSet().distinct().sortedBy { it }.forEach { emit(it) }
}

inline fun <reified T : Comparable<T>> Flow<T>.distinct(): Flow<T> = flow {
    emitAll(flow = toList().distinct().asFlow())
}
