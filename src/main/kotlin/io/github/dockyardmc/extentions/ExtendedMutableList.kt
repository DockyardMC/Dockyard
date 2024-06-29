package io.github.dockyardmc.extentions

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.inventory.ItemStack
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent

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

fun MutableList<Player>.sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
    this.forEach { it.sendTitle(title, subtitle, fadeIn, stay, fadeOut) }
}

fun MutableList<Player>.clearTitle(reset: Boolean = true) {
    this.forEach { it.clearTitle(reset) }
}

fun MutableList<Player>.setTabHeader(header: String) {
    this.forEach { it.tabListHeader.value = header.toComponent() }
}

fun MutableList<Player>.setTabFooter(footer: String) {
    this.forEach { it.tabListFooter.value = footer.toComponent() }
}

fun MutableList<Player>.setGameMode(gameMode: GameMode) {
    this.forEach { it.gameMode.value = gameMode }
}

fun MutableList<Entity>.addViewer(player: Player) {
    this.forEach { it.addViewer(player) }
}

fun MutableList<Entity>.removeViewer(player: Player, isDisconnect: Boolean = false) {
    this.forEach { it.removeViewer(player, isDisconnect) }
}

fun MutableList<Player>.setCanFly(canFly: Boolean) {
    this.forEach { it.canFly.value = canFly }
}

fun MutableList<Player>.setIsFlying(isFlying: Boolean) {
    this.forEach { it.isFlying.value = isFlying }
}

fun MutableList<Player>.give(itemStack: ItemStack) {
    this.forEach { it.give(itemStack) }
}

fun MutableList<Player>.clearInventory() {
    this.forEach { it.clearInventory() }
}

fun <E> MutableList<E>.addIfNotPresent(target: E) {
    if(!this.contains(target)) this.add(target)
}

fun <E> MutableList<E>.removeIfPresent(target: E) {
    if(this.contains(target)) this.remove(target)
}