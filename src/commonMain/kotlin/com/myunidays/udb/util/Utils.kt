package com.myunidays.udb.util

fun String.splitOnSpacing(): List<String> = split("\\s+".toRegex()).filterNot { it.isBlank() }

inline fun <reified A : Enum<A>> matchByName(name: String): A {
    return enumValues<A>().single { it.name.equals(name, ignoreCase = true) }
}
