package io.github.dockyardmc.utils

import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

abstract class Viewable {

    val viewers: ObjectOpenHashSet<Player> = ObjectOpenHashSet()
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