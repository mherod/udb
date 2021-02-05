package com.myunidays.udb.util

fun String.splitOnSpacing(): List<String> = split("\\s+".toRegex()).filterNot { it.isBlank() }

inline fun <reified A : Enum<A>> matchByName(name: String): A {
    return enumValues<A>().single { it.name.equals(name, ignoreCase = true) }
}

fun String?.isNotNullOrBlank(): Boolean = isNullOrBlank().not()

infix fun String.attributeString(name: String): String =
    "$name=\"([^\"]*)\"".toRegex().find(this)?.groupValues?.lastOrNull().orEmpty()

infix fun String.attributeBoolean(name: String): Boolean = attributeString(name).toBoolean()
