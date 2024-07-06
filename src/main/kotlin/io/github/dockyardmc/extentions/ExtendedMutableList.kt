package io.github.dockyardmc.extentions

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.setSkin
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import java.util.UUID

fun List<Player>.sendMessage(message: String) {
    this.forEach { it.sendMessage(message) }
}

fun List<Player>.sendMessage(message: Component) {
    this.forEach { it.sendMessage(message) }
}

fun List<Player>.sendPacket(packet: ClientboundPacket) {
    this.forEach { it.sendPacket(packet) }
}

fun List<Player>.sendActionBar(message: String) {
    this.forEach { it.sendActionBar(message) }
}

fun List<Player>.sendActionBar(message: Component) {
    this.forEach { it.sendActionBar(message) }
}

fun List<Player>.sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
    this.forEach { it.sendTitle(title, subtitle, fadeIn, stay, fadeOut) }
}

fun List<Player>.clearTitle(reset: Boolean = true) {
    this.forEach { it.clearTitle(reset) }
}

fun List<Player>.setTabHeader(header: String) {
    this.forEach { it.tabListHeader.value = header.toComponent() }
}

fun List<Player>.setTabFooter(footer: String) {
    this.forEach { it.tabListFooter.value = footer.toComponent() }
}

fun List<Player>.setGameMode(gameMode: GameMode) {
    this.forEach { it.gameMode.value = gameMode }
}

fun List<Entity>.addViewer(player: Player) {
    this.forEach { it.addViewer(player) }
}

fun List<Entity>.removeViewer(player: Player, isDisconnect: Boolean = false) {
    this.forEach { it.removeViewer(player, isDisconnect) }
}

fun List<Player>.setCanFly(canFly: Boolean) {
    this.forEach { it.canFly.value = canFly }
}

fun List<Player>.setSkin(uuid: UUID) {
    this.forEach { it.setSkin(uuid) }
}

fun List<Player>.setSkin(username: String) {
    this.forEach { it.setSkin(username) }
}

fun List<Player>.setIsFlying(isFlying: Boolean) {
    this.forEach { it.isFlying.value = isFlying }
}

//fun List<Player>.give(itemStack: ItemStack) {
//    this.forEach { it.give(itemStack) }
//}
//
//fun List<Player>.clearInventory() {
//    this.forEach { it.clearInventory() }
//}

fun <E> MutableList<E>.addIfNotPresent(target: E) {
    if(!this.contains(target)) this.add(target)
}

fun <E> MutableList<E>.removeIfPresent(target: E) {
    if(this.contains(target)) this.remove(target)
}