package io.github.dockyardmc.extentions

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.player.PersistentPlayer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket

val BindableList<PersistentPlayer>.onlinePlayers: List<Player> get() {
    val players = mutableListOf<Player>()
    val onlinePersistent = this.values.filter { it.toPlayer() != null }
    onlinePersistent.forEach { players.add(it.toPlayer()!!) }

    return players.toList()
}

fun BindableList<Player>.sendPacket(packet: ClientboundPacket) {
    values.sendPacket(packet)
}