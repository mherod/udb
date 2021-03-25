package dev.herod.kmpp.networking.model

data class ArpEntry(
    val name: String,
    val mac: String,
    val netInterface: String,
    val address: String,
)
