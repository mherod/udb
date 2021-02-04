package com.myunidays.udb.adb

import com.myunidays.udb.Injectable
import com.myunidays.udb.util.runOrNull
import kotlin.reflect.KProperty

inline operator fun <reified T : Any> Injectable.getValue(any: Any, property: KProperty<*>): T =
    javaClass.declaredMethods
        .mapNotNull { method ->
            runOrNull { method.invoke(this) as T }
        }.single()
