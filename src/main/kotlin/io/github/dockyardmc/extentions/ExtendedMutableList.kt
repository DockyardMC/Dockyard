package io.github.dockyardmc.extentions

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.setSkin
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import java.util.UUID

fun Collection<Player>.sendMessage(message: String) {
    this.forEach { it.sendMessage(message) }
}

fun Collection<Player>.sendMessage(message: Component) {
    this.toList().forEach { it.sendMessage(message) }
}

fun Collection<Player>.sendPacket(packet: ClientboundPacket) {
    this.toList().forEach { it.sendPacket(packet) }
}

fun Collection<Player>.sendActionBar(message: String) {
    this.toList().forEach { it.sendActionBar(message) }
}

fun Collection<Player>.sendActionBar(message: Component) {
    this.toList().forEach { it.sendActionBar(message) }
}

fun Collection<Player>.sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
    this.toList().forEach { it.sendTitle(title, subtitle, fadeIn, stay, fadeOut) }
}

fun Collection<Player>.clearTitle(reset: Boolean = true) {
    this.toList().forEach { it.clearTitle(reset) }
}

fun Collection<Player>.setTabHeader(header: String) {
    this.toList().forEach { it.tabListHeader.value = header.toComponent() }
}

fun Collection<Player>.setTabFooter(footer: String) {
    this.toList().forEach { it.tabListFooter.value = footer.toComponent() }
}

fun Collection<Player>.setGameMode(gameMode: GameMode) {
    this.toList().forEach { it.gameMode.value = gameMode }
}

fun Collection<Entity>.addViewer(player: Player) {
    this.toList().forEach { it.addViewer(player) }
}

fun Collection<Entity>.removeViewer(player: Player, isDisconnect: Boolean = false) {
    this.toList().forEach { it.removeViewer(player) }
}

fun Collection<Player>.setCanFly(canFly: Boolean) {
    this.toList().forEach { it.canFly.value = canFly }
}

fun Collection<Player>.setSkin(uuid: UUID) {
    this.toList().forEach { it.setSkin(uuid) }
}

fun Collection<Player>.setSkin(username: String) {
    this.toList().forEach { it.setSkin(username) }
}

fun Collection<Player>.setIsFlying(isFlying: Boolean) {
    this.toList().forEach { it.isFlying.value = isFlying }
}

//fun Collection<Player>.give(itemStack: ItemStack) {
//    this.forEach { it.give(itemStack) }
//}
//
//fun Collection<Player>.clearInventory() {
//    this.forEach { it.clearInventory() }
//}

fun <E> MutableCollection<E>.addIfNotPresent(target: E) {
    if(!this.contains(target)) this.add(target)
}

fun <E> MutableCollection<E>.removeIfPresent(target: E) {
    if(this.contains(target)) this.remove(target)
}