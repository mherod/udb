package com.myunidays.udb.adb.model

data class AdbLogcatLine(
    val date: String,
    val time: String,
    val pid: Int,
    val tid: Int,
    val priority: Priority,
    val tag: String,
    val message: String,
) {
    enum class Priority(private val label: String) {
        V("verbose"),
        D("debug"),
        I("info"),
        W("warning"),
        E("error"),
        A("assert");

        override fun toString(): String = "Priority(label='$label')"
    }
}
