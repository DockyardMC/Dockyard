package io.github.dockyardmc.extentions

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.inventory.ContainerInventory
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.Player.ChestAnimation
import io.github.dockyardmc.player.setSkin
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import java.util.UUID

fun Collection<Player>.teleport(location: Location) {
    this.forEach { player -> player.teleport(location) }
}

fun Collection<Player>.clearInventory() {
    this.forEach { player -> player.clearInventory() }
}

fun Collection<Player>.giveItem(vararg itemStack: ItemStack) {
    this.forEach { player -> player.give(*itemStack) }
}

fun Collection<Player>.giveItem(vararg item: Item) {
    this.forEach { player -> player.give(*item) }
}

fun Collection<Player>.openInventory(inventory: ContainerInventory) {
    this.forEach { player -> player.openInventory(inventory) }
}

fun Collection<Player>.playTotemAnimation(customModelData: Int? = null) {
    this.forEach { player -> player.playTotemAnimation(customModelData) }
}

fun Collection<Player>.setCooldown(item: Item, cooldownTicks: Int) {
    this.forEach { player -> player.setCooldown(item, cooldownTicks) }
}

fun Collection<Player>.setCooldown(group: String, cooldownTicks: Int) {
    this.forEach { player -> player.setCooldown(group, cooldownTicks) }
}

fun Collection<Player>.playChestAnimation(chestLocation: Location, animation: ChestAnimation) {
    this.forEach { player -> player.playChestAnimation(chestLocation, animation) }
}

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

fun <E> MutableCollection<E>.addIfNotPresent(target: E) {
    if(!this.contains(target)) this.add(target)
}

fun <E> MutableCollection<E>.removeIfPresent(target: E) {
    if(this.contains(target)) this.remove(target)
}

fun <E> MutableList<E>.consumeFirstOrNull(): E? {
    val first = this.firstOrNull()
    if(first != null) this.remove(first)
    return first
}

fun <E> MutableList<E>.consumeFirst(): E {
    return this.consumeFirstOrNull() ?: throw NoSuchElementException("mutable list is empty for consuming first element!")
}
