package com.myunidays.udb.util

import com.myunidays.udb.exec
import kotlinx.coroutines.flow.Flow

fun kill(pid: Int): Flow<String> = exec("kill $pid")
