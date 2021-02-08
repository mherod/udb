package com.myunidays.udb

import kotlinx.coroutines.flow.Flow

expect fun bash(command: String): Flow<String>
