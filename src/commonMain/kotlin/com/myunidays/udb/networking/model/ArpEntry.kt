package com.myunidays.udb.networking.model

data class ArpEntry(
    val name: String,
    val mac: String,
    val netInterface: String,
    val address: String,
)
