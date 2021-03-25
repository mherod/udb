package dev.herod.kmpp

import dev.herod.kdi.Injectable
import dev.herod.kmpp.networking.ArpClient
import dev.herod.kmpp.networking.ArpProcessClient
import dev.herod.kmpp.networking.BonjourClient
import dev.herod.kmpp.networking.BonjourProcessClient

object Container : Injectable {
    override val arpClient: ArpClient by lazy(::ArpProcessClient)
    override val bonjourClient: BonjourClient by lazy(::BonjourProcessClient)
}
