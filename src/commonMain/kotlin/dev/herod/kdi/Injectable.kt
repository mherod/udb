package dev.herod.kdi

import dev.herod.kmpp.networking.ArpClient
import dev.herod.kmpp.networking.BonjourClient

interface Injectable {
    val arpClient: ArpClient
    val bonjourClient: BonjourClient
}
