package com.myunidays.udb.util

inline fun <reified T> runOrNull(function: () -> T): T? = runCatching(function).getOrNull()
