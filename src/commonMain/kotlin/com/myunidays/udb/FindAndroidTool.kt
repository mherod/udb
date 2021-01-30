package com.myunidays.udb

import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.single

fun findAndroidTool(tool: String) = runBlocking {
    val easy = exec("which $tool").single()
    if (easy.startsWith("/") && easy.endsWith(tool)) {
        return@runBlocking easy
    }
    val androidHome = envVar("ANDROID_HOME")
    if (androidHome.isNullOrBlank()) {
        error("couldn't find $tool. try setting ANDROID_HOME environmental variable")
    }
    return@runBlocking exec(
        command = "find \"$androidHome\" -type f -name \"$tool\""
    ).reduce { accumulator, value ->
        when {
            value.length < accumulator.length -> value
            else -> accumulator
        }.trim()
    }
}
