package io.github.dockyardmc.utils

import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket

abstract class Viewable {

    val viewers: MutableList<Player> = mutableListOf()
    val blockedViewers: MutableList<Player> = mutableListOf()
    abstract var autoViewable: Boolean

    abstract fun addViewer(player: Player)

    abstract fun removeViewer(player: Player)

    fun isViewer(player: Player): Boolean {
        return viewers.contains(player)
    }

    fun sendPacketToViewers(packet: ClientboundPacket) {
        viewers.sendPacket(packet)
    }

}