package io.github.dockyardmc.player

import cz.lukynka.BindableList
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import java.util.UUID

//TODO Document this
data class PersistentPlayer(
    val uuid: UUID
) {
    //TODO micro-optimization: make uuid to player map
    fun toPlayer(): Player = PlayerManager.players.first { it.uuid == uuid }
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

fun BindableList<Player>.toPersistent(): BindableList<PersistentPlayer> {
    val arrOut = mutableListOf<PersistentPlayer>()
    this.values.forEach { arrOut.add(PersistentPlayer(it.uuid)) }
    return BindableList<PersistentPlayer>(arrOut)
}

fun BindableList<PersistentPlayer>.toPlayer(): BindableList<Player> {
    val arrOut = mutableListOf<Player>()
    this.values.forEach { arrOut.add(it.toPlayer()) }
    return BindableList<Player>(arrOut)
}

fun BindableList<PersistentPlayer>.sendPacket(packet: ClientboundPacket) {
    this.values.forEach { it.toPlayer().sendPacket(packet) }
}

fun MutableList<PersistentPlayer>.sendPacket(packet: ClientboundPacket) {
    this.forEach { it.toPlayer().sendPacket(packet) }
}

fun PersistentPlayer.sendPacket(packet: ClientboundPacket) {
    this.toPlayer().sendPacket(packet)
}

operator fun BindableList<PersistentPlayer>.contains(target: Player): Boolean =
    this.values.contains(target.toPersistent())

fun BindableList<PersistentPlayer>.addIfNotPresent(target: Player) {
    this.addIfNotPresent(target.toPersistent())
}
fun BindableList<PersistentPlayer>.removeIfPresent(target: Player) {
    this.removeIfPresent(target.toPersistent())
}

fun Player.toPersistent(): PersistentPlayer = PersistentPlayer(this.uuid)

fun BindableList<PersistentPlayer>.add(target: Player) {
    this.add(target.toPersistent())
}