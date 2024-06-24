package io.github.dockyardmc.player

import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import java.util.UUID

//TODO Document this
data class PersistentPlayer(
    val uuid: UUID
) {
    fun toPlayer(): Player {
        //TODO micro-optimization: make uuid to player map
        return PlayerManager.players.first { it.uuid == uuid }
    }
}

fun MutableList<Player>.toPersistent(): MutableList<PersistentPlayer> {
    val arrOut = mutableListOf<PersistentPlayer>()
    this.forEach { arrOut.add(PersistentPlayer(it.uuid)) }
    return arrOut
}

fun MutableList<PersistentPlayer>.toPlayer(): MutableList<Player> {
    val arrOut = mutableListOf<Player>()
    this.forEach { arrOut.add(it.toPlayer()) }
    return arrOut
}

fun BindableMutableList<Player>.toPersistent(): BindableMutableList<PersistentPlayer> {
    val arrOut = mutableListOf<PersistentPlayer>()
    this.values.forEach { arrOut.add(PersistentPlayer(it.uuid)) }
    return BindableMutableList<PersistentPlayer>(arrOut)
}

fun BindableMutableList<PersistentPlayer>.toPlayer(): BindableMutableList<Player> {
    val arrOut = mutableListOf<Player>()
    this.values.forEach { arrOut.add(it.toPlayer()) }
    return BindableMutableList<Player>(arrOut)
}

fun BindableMutableList<PersistentPlayer>.sendPacket(packet: ClientboundPacket) {
    this.values.forEach { it.toPlayer().sendPacket(packet) }
}

fun MutableList<PersistentPlayer>.sendPacket(packet: ClientboundPacket) {
    this.forEach { it.toPlayer().sendPacket(packet) }
}

fun PersistentPlayer.sendPacket(packet: ClientboundPacket) {
    this.toPlayer().sendPacket(packet)
}

fun BindableMutableList<PersistentPlayer>.contains(target: Player): Boolean {
    return this.values.contains(target.toPersistent())
}

fun Player.toPersistent(): PersistentPlayer {
    return PersistentPlayer(this.uuid)
}

fun BindableMutableList<PersistentPlayer>.add(target: Player) {
    this.add(target.toPersistent())
}