package com.myunidays.udb

import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.singleOrNull

fun findAndroidTool(tool: String): String = runBlocking {
    val easy = exec("which $tool").singleOrNull().orEmpty()
    if (easy.startsWith("/") && easy.endsWith(tool)) {
        return@runBlocking easy
    }
    val androidHome = envVar("ANDROID_HOME")
    if (androidHome.isNullOrBlank()) {
        error("couldn't find $tool. try setting ANDROID_HOME environmental variable")
    }
    val searchPath = when (tool) {
        "emulator" -> "$androidHome/emulator"
        else -> androidHome
    }
    return@runBlocking findFile(
        searchPath = searchPath,
        fileName = tool
    ).onEmpty {
        error("couldn't find $tool. Check it is installed in your Android SDK")
    }.reduce { accumulator, value ->
        when {
            value.length < accumulator.length -> value
            else -> accumulator
        }.trim()
    }
}
