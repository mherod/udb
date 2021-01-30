package com.myunidays.udb.cli

inline class ChangePair<T>(private val pair: Pair<T, T>) {
    operator fun component1(): T = pair.first
    operator fun component2(): T = pair.second
}
