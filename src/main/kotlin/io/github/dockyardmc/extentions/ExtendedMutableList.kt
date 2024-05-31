package io.github.dockyardmc.extentions

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component

fun MutableList<Player>.sendMessage(message: String) {
    this.forEach { it.sendMessage(message) }
}

fun MutableList<Player>.sendMessage(message: Component) {
    this.forEach { it.sendMessage(message) }
}

fun MutableList<Player>.sendPacket(packet: ClientboundPacket) {
    this.forEach { it.sendPacket(packet) }
}

fun MutableList<Player>.sendActionBar(message: String) {
    this.forEach { it.sendActionBar(message) }
}

fun MutableList<Player>.sendActionBar(message: Component) {
    this.forEach { it.sendActionBar(message) }
}
