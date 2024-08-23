package io.github.dockyardmc.world

import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundInitializeWorldBorderPacket

class WorldBorder(val world: World) {

    var diameter: Double = 2.9999984E7
    var warningBlocks: Int = 10
    var warningTime: Int = 1

    fun setSize(diameter: Double, speed: Long) {
        val packet = ClientboundInitializeWorldBorderPacket(this.diameter, diameter, speed, warningBlocks, warningTime)
        PlayerManager.sendToEveryoneInWorld(world, packet)
    }
}